package rs.chat.unit.controllers;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.controllers.UserController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.dtos.UserDto;
import rs.chat.domain.entity.mappers.OpenedSessionMapper;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.GroupService;
import rs.chat.domain.service.SessionService;
import rs.chat.domain.service.UserService;
import rs.chat.exceptions.BadRequestException;
import rs.chat.exceptions.NotFoundException;
import rs.chat.utils.Constants;
import rs.chat.utils.factories.DefaultFactory;
import rs.chat.utils.security.annotations.WithMockAdmin;
import rs.chat.utils.security.annotations.WithMockStudent;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.router.Routes.GetRoute.OPENED_SESSIONS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.USERS_URL;
import static rs.chat.router.Routes.GetRoute.USER_ID_BY_USERNAME_URL;
import static rs.chat.router.Routes.GetRoute.USER_STATS_URL;
import static rs.chat.router.Routes.PostRoute.DELETE_USER_URL;
import static rs.chat.router.Routes.PostRoute.USER_SAVE_URL;
import static rs.chat.utils.TestConstants.TEST_OBJECT_MAPPER;
import static rs.chat.utils.TestUtils.request;

@WebMvcTest(UserController.class)
class UserControllerTest {
	@Autowired private MockMvc mvc;

	@MockBean private UserService userService;
	@MockBean private SessionService sessionService;
	@MockBean private GroupService groupService;
	@MockBean private ChatService chatService;
	@MockBean private OpenedSessionMapper openedSessionMapper;

	@Test
	@WithMockStudent
	void getUsersWithUsers() throws Exception {
		// Given
		List<UserDto> dtoList = List.of(
				DefaultFactory.INSTANCE.createUserDto(1L, Constants.STUDENT_ROLE),
				DefaultFactory.INSTANCE.createUserDto(2L, Constants.STUDENT_ROLE)
		);

		given(userService.getUsers()).willReturn(dtoList);

		// When
		MockHttpServletResponse response = mvc.perform(get(USERS_URL))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(dtoList));
	}

	@Test
	@WithMockStudent
	void getUsersWithoutUsers() throws Exception {
		// Given
		List<UserDto> dtoList = List.of();
		given(userService.getUsers()).willReturn(dtoList);

		// When
		MockHttpServletResponse response = mvc.perform(get(USERS_URL))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(dtoList));
	}

	@Test
	@WithMockAdmin
	void saveUserOk() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Group group = DefaultFactory.INSTANCE.createGroup(1L, "Global");
		Chat chat = DefaultFactory.INSTANCE.createChat(1L, "name", "group");

		given(userService.createUser(any(User.class))).willReturn(user);
		given(groupService.getGroupByName(any(String.class))).willReturn(group);
		given(chatService.getByName(any(String.class))).willReturn(chat);

		Map<String, Map<String, Object>> userBody = Map.of("user", Map.of(
				"email", user.getEmail(),
				"username", user.getUsername(),
				"fullName", user.getFullName(),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"role", user.getRole(),
				"agreeTerms", Boolean.TRUE)
		);

		// When
		MockHttpServletResponse response = mvc.perform(request(POST, USER_SAVE_URL)
				                                               .contentType(MediaType.APPLICATION_JSON)
				                                               .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                                      .andExpect(status().isCreated())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString()).isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(""));
	}

	@Test
	@WithMockAdmin
	void saveUserEmailTaken() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(null, Constants.STUDENT_ROLE);
		Group group = DefaultFactory.INSTANCE.createGroup(1L, "Global");
		Chat chat = DefaultFactory.INSTANCE.createChat(1L, "name", "group");

		given(userService.createUser(any(User.class))).willReturn(user);
		given(groupService.getGroupByName(any(String.class))).willReturn(group);
		given(chatService.getByName(any(String.class))).willReturn(chat);

		Map<String, Map<String, Object>> userBody = Map.of("user", Map.of(
				"email", user.getEmail(),
				"username", user.getUsername(),
				"fullName", user.getFullName(),
				"password", user.getPassword(),
				"confirmPassword", user.getPassword(),
				"role", user.getRole(),
				"agreeTerms", Boolean.TRUE)
		);

		given(userService.createUser(any(User.class)))
				.willThrow(new BadRequestException("Email %s taken".formatted(user.getEmail())));

		// When
		// Then
		assertThatThrownBy(() -> mvc.perform(request(POST, USER_SAVE_URL)
				                                     .contentType(MediaType.APPLICATION_JSON)
				                                     .content(TEST_OBJECT_MAPPER.writeValueAsString(userBody)))
		                            .andExpect(status().isBadRequest())
		                            .andReturn()
		                            .getResponse())
				.cause()
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Email %s taken", user.getEmail());
	}

	//* Since these tests are unit tests for the controller layer, we could not verify that the user
	//* is one without rights. We could only verify the functionality of the controller.
	//* This role check must be done in the integration tests.

	@Test
	@WithMockStudent
	void openedSessionsSingleSession() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.STUDENT_ROLE);
		List<Session> ips = Stream.of(DefaultFactory.INSTANCE.createSession(1L, user),
		                              DefaultFactory.INSTANCE.createSession(2L, user),
		                              DefaultFactory.INSTANCE.createSession(3L, user))
		                          .toList();

		given(sessionService.getSessionsByUsername(any(String.class))).willReturn(ips);

		// When
		MockHttpServletResponse response =
				mvc.perform(request(HttpMethod.GET, OPENED_SESSIONS_OF_USER_URL, user.getUsername()))
				   .andExpect(status().isOk())
				   .andReturn()
				   .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(
						ips.stream().map(this.openedSessionMapper::toDto).toList())
				);
	}

	@Test
	@WithMockStudent
	void openedSessionsNoSessions() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.STUDENT_ROLE);
		List<Session> ips = List.of();

		given(sessionService.getSessionsByUsername(any(String.class))).willReturn(ips);

		// When
		MockHttpServletResponse response =
				mvc.perform(request(HttpMethod.GET, OPENED_SESSIONS_OF_USER_URL, user.getUsername()))
				   .andExpect(status().isOk())
				   .andReturn()
				   .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(ips));
	}

	@Test
	@WithMockStudent
	void getIdByUsernameOk() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.STUDENT_ROLE);

		given(userService.getUserByUsername(any(String.class))).willReturn(user);

		// When
		MockHttpServletResponse response =
				mvc.perform(request(HttpMethod.GET, USER_ID_BY_USERNAME_URL, user.getUsername()))
				   .andExpect(status().isOk())
				   .andReturn()
				   .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(user.getId()));
	}

	@Test
	@WithMockStudent
	void getIdByUsernameNotFound() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.STUDENT_ROLE);

		given(userService.getUserByUsername(any(String.class)))
				.willThrow(new NotFoundException("Username not found"));

		// When
		// Then
		assertThatThrownBy(() -> mvc.perform(request(HttpMethod.GET, USER_ID_BY_USERNAME_URL, user.getUsername()))
		                            .andExpect(status().isNotFound())
		                            .andReturn()
		                            .getResponse())
				.cause()
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Username not found");
	}

	@Test
	@WithMockAdmin
	void deleteUserOk() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.TEACHER_ROLE);

		// When
		MockHttpServletResponse response = mvc.perform(request(HttpMethod.DELETE, DELETE_USER_URL, user.getId()))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString()).isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(""));
	}

	@Test
	@WithMockAdmin
	void deleteUserNotFound() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.TEACHER_ROLE);

		willThrow(new NotFoundException("User not found"))
				.given(userService)
				.deleteUser(any(Long.class));
		// Due to the fact that userService.deleteUser() returns void, we cannot use
		// given(userService.deleteUser(any(Long.class))).willThrow(new NotFoundException("User not found"));

		// When
		// Then
		assertThatThrownBy(() -> mvc.perform(request(HttpMethod.DELETE, DELETE_USER_URL, user.getId()))
		                            .andExpect(status().isNotFound())
		                            .andReturn()
		                            .getResponse())
				.cause()
				.isInstanceOf(NotFoundException.class)
				.hasMessage("User not found");
	}

	@Test
	@WithMockStudent
	void getUserStatsOk() throws Exception {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.STUDENT_ROLE);
		JsonObject messageCountByType = new JsonObject();
		messageCountByType.addProperty("TEXT_MESSAGE", 3);
		messageCountByType.addProperty("PDF_MESSAGE", 2);
		messageCountByType.addProperty("IMAGE_MESSAGE", 1);
		user.setMessageCountByType(messageCountByType);

		given(userService.getUserStats(any(String.class))).willReturn(messageCountByType);

		// When
		MockHttpServletResponse response = mvc.perform(request(HttpMethod.GET, USER_STATS_URL, user.getUsername()))
		                                      .andExpect(status().isOk())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString())
				.isEqualTo(TEST_OBJECT_MAPPER.writeValueAsString(messageCountByType));
	}

	@Test
	@WithMockStudent
	void getUserStatsUserNotFound() {
		// Given
		User user = DefaultFactory.INSTANCE.createUser(1L, Constants.STUDENT_ROLE);

		given(userService.getUserStats(any(String.class)))
				.willThrow(new NotFoundException("User not found"));

		// When
		// Then
		assertThatThrownBy(() -> mvc.perform(request(HttpMethod.GET, USER_STATS_URL, user.getUsername()))
		                            .andExpect(status().isNotFound())
		                            .andReturn()
		                            .getResponse())
				.cause()
				.isInstanceOf(NotFoundException.class)
				.hasMessage("User not found");

		//! NOTE: if the test have to catch an exception, the ControllerUtils.performActionThatMayThrowException
		//! method must be used in the controller. Otherwise, the exception will be caught before and the
		//! assertThatThrownBy will fail.
	}
}
