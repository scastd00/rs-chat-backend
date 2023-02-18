package rs.chat.unit.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rs.chat.controllers.UserController;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.dtos.UserDto;
import rs.chat.domain.entity.mappers.UserMapper;
import rs.chat.domain.entity.mappers.UserMapperImpl;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.GroupService;
import rs.chat.domain.service.SessionService;
import rs.chat.domain.service.UserService;
import rs.chat.utils.Constants;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.chat.Constants.TEST_OBJECT_MAPPER;
import static rs.chat.TestUtils.createUserWithRole;
import static rs.chat.router.Routes.GetRoute.USERS_URL;

@WebMvcTest(UserController.class)
class UserControllerTest {
	@Autowired private MockMvc mvc;

	@MockBean private UserService userService;
	@MockBean private SessionService sessionService;
	@MockBean private GroupService groupService;
	@MockBean private ChatService chatService;

	private final UserMapper userMapper = new UserMapperImpl();

	@Test
	@WithMockUser
	void getUsersWithUsers() throws Exception {
		// Given
		UserDto dto1 = userMapper.toDto(createUserWithRole(Constants.STUDENT_ROLE));
		UserDto dto2 = userMapper.toDto(createUserWithRole(Constants.STUDENT_ROLE));
		List<UserDto> dtoList = List.of(dto1, dto2);
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
	@WithMockUser
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
	@WithMockUser
	void saveUserOk() throws Exception {
		// Given
		User user = createUserWithRole(Constants.STUDENT_ROLE);
		given(userService.createUser(user)).willReturn(user);

		// When
		MockHttpServletResponse response = mvc.perform(post(USERS_URL)
				                                               .contentType(MediaType.APPLICATION_JSON)
				                                               .content(TEST_OBJECT_MAPPER.writeValueAsString(user)))
		                                      .andExpect(status().isCreated())
		                                      .andReturn()
		                                      .getResponse();

		// Then
		assertThat(response.getContentAsString()).isEmpty(); // No content returned
	}

	@Test
	@WithMockUser(roles = Constants.STUDENT_ROLE)
	void saveUserForbidden() throws Exception {
		// Given
		User user = createUserWithRole(Constants.STUDENT_ROLE);
		given(userService.createUser(user)).willReturn(user);

		// When
		MockHttpServletResponse response = mvc.perform(post(USERS_URL)
				                                               .contentType(MediaType.APPLICATION_JSON)
				                                               .content(TEST_OBJECT_MAPPER.writeValueAsString(user)))
		                                      .andExpect(status().isForbidden())
		                                      .andReturn()
		                                      .getResponse();
		// A student user is not allowed to create a new user.

		// Then
		assertThat(response.getContentAsString()).isEmpty(); // No content returned
	}
}
