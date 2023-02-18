package rs.chat.unit.controllers;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.controllers.UserController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.dtos.UserDto;
import rs.chat.domain.entity.mappers.UserMapper;
import rs.chat.domain.entity.mappers.UserMapperImpl;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.GroupService;
import rs.chat.domain.service.SessionService;
import rs.chat.domain.service.UserService;
import rs.chat.utils.Constants;
import rs.chat.utils.security.annotations.WithMockAdmin;
import rs.chat.utils.security.annotations.WithMockStudent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.router.Routes.GetRoute.USERS_URL;
import static rs.chat.router.Routes.PostRoute.USER_SAVE_URL;
import static rs.chat.utils.TestConstants.TEST_OBJECT_MAPPER;
import static rs.chat.utils.TestUtils.createUserWithRole;
import static rs.chat.utils.TestUtils.request;

@WebMvcTest(UserController.class)
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration
class UserControllerTest {
	@Autowired private MockMvc mvc;

	@MockBean private UserService userService;
	@MockBean private SessionService sessionService;
	@MockBean private GroupService groupService;
	@MockBean private ChatService chatService;

	private final UserMapper userMapper = new UserMapperImpl();

	@Test
	@WithMockStudent
	void getUsersWithUsers() throws Exception {
		// Given
		UserDto dto1 = userMapper.toDto(createUserWithRole(Constants.STUDENT_ROLE));
		UserDto dto2 = userMapper.toDto(createUserWithRole(Constants.STUDENT_ROLE));
		List<UserDto> dtoList = List.of(dto1, dto2);
		BDDMockito.given(userService.getUsers()).willReturn(dtoList);

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
		BDDMockito.given(userService.getUsers()).willReturn(dtoList);

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
		User user = createUserWithRole(Constants.STUDENT_ROLE);
		Group group = new Group(1L, "Global", Collections.emptySet());
		Chat chat = new Chat(
				1L, "name", "group", "folder",
				new JsonObject(), "25wcv9A", "group-1",
				Collections.emptySet()
		);

		BDDMockito.given(userService.createUser(Mockito.any(User.class))).willReturn(user);
		BDDMockito.given(groupService.getGroupByName(Mockito.any(String.class))).willReturn(group);
		BDDMockito.given(chatService.getByName(Mockito.any(String.class))).willReturn(chat);

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
}
