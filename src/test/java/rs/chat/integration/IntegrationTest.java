package rs.chat.integration;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.GroupRepository;
import rs.chat.domain.repository.SessionRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.domain.service.UserService;
import rs.chat.exceptions.MinimumRequirementsNotMetException;
import rs.chat.utils.Constants;
import rs.chat.utils.SaveDefaultsToDB;
import rs.chat.utils.TestUtils;
import rs.chat.utils.factories.DefaultFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;
import static rs.chat.utils.TestConstants.TEST_OBJECT_MAPPER;
import static rs.chat.utils.TestConstants.TEST_PASSWORD;
import static rs.chat.utils.TestUtils.request;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IntegrationTest {
	@Autowired private MockMvc mockMvc;

	@Autowired private UserRepository userRepository;
	@Autowired private SessionRepository sessionRepository;
	@Autowired private ChatRepository chatRepository;
	@Autowired private GroupRepository groupRepository;
	@Autowired private UserService userService;

	private Session studentSession;
	private Session teacherSession;
	private Session adminSession;

	@BeforeEach
	void setUp() {
		Map<String, Session> sessionMap = SaveDefaultsToDB.saveDefaults(
				userRepository, groupRepository, chatRepository,
				null, null, sessionRepository
		);

		studentSession = sessionMap.get(Constants.STUDENT_ROLE);
		teacherSession = sessionMap.get(Constants.TEACHER_ROLE);
		adminSession = sessionMap.get(Constants.ADMIN_ROLE);
	}

	@AfterEach
	void tearDown() {
		sessionRepository.deleteAll();
		chatRepository.deleteAll();
		groupRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void testLogin() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		userService.createUser(user); // Save the user to DB to be able to log in

		// When
		String response = mockMvc.perform(request(HttpMethod.POST, LOGIN_URL)
				                                  .contentType(MediaType.APPLICATION_JSON)
				                                  .content(TEST_OBJECT_MAPPER.writeValueAsString(Map.of(
						                                  "username", user.getUsername(),
						                                  "password", TEST_PASSWORD,
						                                  "remember", Boolean.FALSE
				                                  ))))
		                         .andExpect(status().isOk())
		                         .andReturn()
		                         .getResponse()
		                         .getContentAsString();

		// Then
		JsonObject jsonResponse = TestUtils.parseJson(response);
		String responseToken = jsonResponse.get("session").getAsJsonObject()
		                                   .get("token").getAsString();

		assertThat(sessionRepository.findByToken(responseToken)).isPresent();
	}

	@Test
	void testRegister() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Map<String, Object> userBody = Map.of(
				"email", user.getEmail(),
				"username", user.getUsername(),
				"fullName", user.getFullName(),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"agreeTerms", Boolean.TRUE
		);

		// When
		String response = mockMvc.perform(request(HttpMethod.POST, REGISTER_URL)
				                                  .contentType(MediaType.APPLICATION_JSON)
				                                  .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                         .andExpect(status().isOk())
		                         .andReturn()
		                         .getResponse()
		                         .getContentAsString();

		// Then
		// Check that the user saved in DB is the same as the one we sent
		assertThat(userRepository.findByUsername(user.getUsername())).isPresent();
		// All other fields checked below

		// Check the response content if it matches with the data stored in DB for the user.
		JsonObject jsonResponse = TestUtils.parseJson(response);
		String responseToken = jsonResponse.get("session").getAsJsonObject()
		                                   .get("token").getAsString();
		JsonObject responseUser = jsonResponse.get("user").getAsJsonObject();
		String responseUsername = responseUser.get("username").getAsString();

		assertThat(sessionRepository.findByToken(responseToken)).isPresent();
		assertThat(userRepository.findByUsername(responseUsername))
				.isPresent()
				.get()
				.hasFieldOrPropertyWithValue("email", responseUser.get("email").getAsString())
				.hasFieldOrPropertyWithValue("username", responseUsername)
				.hasFieldOrPropertyWithValue("fullName", responseUser.get("fullName").getAsString());
	}

	@Test
	void testRegisterTermsNotAccepted() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Map<String, Object> userBody = Map.of(
				"email", user.getEmail(),
				"username", user.getUsername(),
				"fullName", user.getFullName(),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"agreeTerms", Boolean.FALSE
		);

		// When
		assertThatThrownBy(() -> mockMvc.perform(request(HttpMethod.POST, REGISTER_URL)
				                                         .contentType(MediaType.APPLICATION_JSON)
				                                         .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                                .andExpect(status().isBadRequest())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString())
				.cause()
				.isInstanceOf(MinimumRequirementsNotMetException.class)
				.hasMessage("You must agree to the terms and conditions.");
	}

	@Test
	void testRegisterWrongEmail() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Map<String, Object> userBody = Map.of(
				"email", "wrongEmail",
				"username", user.getUsername(),
				"fullName", user.getFullName(),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"agreeTerms", Boolean.TRUE
		);

		// When
		assertThatThrownBy(() -> mockMvc.perform(request(HttpMethod.POST, REGISTER_URL)
				                                         .contentType(MediaType.APPLICATION_JSON)
				                                         .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                                .andExpect(status().isBadRequest())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString())
				.cause()
				.isInstanceOf(MinimumRequirementsNotMetException.class)
				.hasMessage("The email is invalid.");
	}

	@Test
	void testRegisterWrongUsernameLessThanLength() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Map<String, Object> userBody = Map.of(
				"email", user.getEmail(),
				"username", "a",
				"fullName", user.getFullName(),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"agreeTerms", Boolean.TRUE
		);

		// When
		assertThatThrownBy(() -> mockMvc.perform(request(HttpMethod.POST, REGISTER_URL)
				                                         .contentType(MediaType.APPLICATION_JSON)
				                                         .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                                .andExpect(status().isBadRequest())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString())
				.cause()
				.isInstanceOf(MinimumRequirementsNotMetException.class)
				.hasMessage("The size of \"username\" must be greater than or equal to 5. The given size is 1");
	}

	@Test
	void testRegisterWrongUsernameMoreThanLength() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Map<String, Object> userBody = Map.of(
				"email", user.getEmail(),
				"username", RandomStringUtils.randomAlphabetic(16),
				"fullName", user.getFullName(),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"agreeTerms", Boolean.TRUE
		);

		// When
		assertThatThrownBy(() -> mockMvc.perform(request(HttpMethod.POST, REGISTER_URL)
				                                         .contentType(MediaType.APPLICATION_JSON)
				                                         .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                                .andExpect(status().isBadRequest())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString())
				.cause()
				.isInstanceOf(MinimumRequirementsNotMetException.class)
				.hasMessage("The size of \"username\" must be less than or equal to 15. The given size is 16");
	}

	@Test
	void testRegisterWrongFullNameMoreThanLength() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Map<String, Object> userBody = Map.of(
				"email", user.getEmail(),
				"username", user.getUsername(),
				"fullName", RandomStringUtils.randomAlphabetic(101),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"agreeTerms", Boolean.TRUE
		);

		// When
		assertThatThrownBy(() -> mockMvc.perform(request(HttpMethod.POST, REGISTER_URL)
				                                         .contentType(MediaType.APPLICATION_JSON)
				                                         .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                                .andExpect(status().isBadRequest())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString())
				.cause()
				.isInstanceOf(MinimumRequirementsNotMetException.class)
				.hasMessage("The size of \"fullName\" must be less than or equal to 100. The given size is 101");
	}

	@Test
	void testRegisterWrongFullNameContainsSQL() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Map<String, Object> userBody = Map.of(
				"email", user.getEmail(),
				"username", user.getUsername(),
				"fullName", "0' OR 1=1; --",
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"agreeTerms", Boolean.TRUE
		);

		// When
		assertThatThrownBy(() -> mockMvc.perform(request(HttpMethod.POST, REGISTER_URL)
				                                         .contentType(MediaType.APPLICATION_JSON)
				                                         .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                                .andExpect(status().isBadRequest())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString())
				.cause()
				.isInstanceOf(MinimumRequirementsNotMetException.class)
				.hasMessage("The \"fullName\" contains malicious code."); //? Is this message correct?
	}
}
