package rs.chat.controllers;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.User;
import rs.chat.domain.service.ChatService;
import rs.chat.domain.service.GroupService;
import rs.chat.domain.service.SessionService;
import rs.chat.domain.service.UserService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.net.smtp.MailSender;
import rs.chat.policies.Policies;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.net.http.HttpResponse.created;
import static rs.chat.router.Routes.GetRoute.OPENED_SESSIONS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.USERS_URL;
import static rs.chat.router.Routes.GetRoute.USER_ID_BY_USERNAME_URL;
import static rs.chat.router.Routes.GetRoute.USER_STATS_URL;
import static rs.chat.router.Routes.PostRoute.DELETE_USER_URL;
import static rs.chat.router.Routes.PostRoute.USER_SAVE_URL;

/**
 * Controller that manages all user-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final SessionService sessionService;
	private final GroupService groupService;
	private final ChatService chatService;

	/**
	 * Returns all users.
	 *
	 * @param response response containing all users.
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(USERS_URL)
	public void getUsers(HttpServletResponse response) throws IOException {
		HttpResponse.send(response, HttpStatus.OK, this.userService.getUsers());
	}

	/**
	 * Saves a new user.
	 *
	 * @param request  request containing the user to be saved.
	 * @param response response containing the saved user.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(USER_SAVE_URL)
	public void saveUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpRequest httpRequest = new HttpRequest(request);
		JsonObject user = (JsonObject) httpRequest.body().get("user");

		User savedUser = ControllerUtils.performActionThatMayThrowException(response, () -> {
			Policies.checkRegister(user);

			return this.userService.createUser(
					new User(
							null, // id
							user.get("username").getAsString().trim(), // username
							user.get("password").getAsString().trim(), // password
							user.get("email").getAsString().trim(), // email
							user.get("fullName").getAsString().trim(), // fullName
							null, // age
							null, // birthdate
							user.get("role").getAsString().trim(), // role
							null, // blockUntil
							null, // passwordCode
							new JsonObject(), // messageCountByType
							emptySet(), // teacherSubjects
							Set.of(this.groupService.getGroupByName("Global")), // groups
							emptySet(), // sessions
							emptySet(), // files
							Set.of(this.chatService.getByName("Global")), // chats
							emptySet(), // studentSubjects
							emptySet() // badges
					)
			);
		});

		HttpResponse.send(created(response, USER_SAVE_URL), HttpStatus.CREATED, HttpResponseBody.EMPTY);
		MailSender.sendRegistrationEmailBackground(savedUser.getEmail(), savedUser.getUsername());
	}

	/**
	 * Returns all opened sessions of user with given username.
	 *
	 * @param response response containing all opened sessions of user with given username.
	 * @param username username of user.
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(OPENED_SESSIONS_OF_USER_URL)
	public void openedSessions(HttpServletResponse response,
	                           @PathVariable String username) throws IOException {
		List<String> sessionsOfUser = this.sessionService.getSrcIpOfUserSessions(username);

		HttpResponse.send(response, HttpStatus.OK, sessionsOfUser);
	}

	@GetMapping(USER_ID_BY_USERNAME_URL)
	public void getIdByUsername(HttpServletResponse response,
	                            @PathVariable String username) throws IOException {
		User user = ControllerUtils.performActionThatMayThrowException(
				response, () -> this.userService.getUserByUsername(username)
		);

		HttpResponse.send(response, HttpStatus.OK, user.getId());
	}

	@DeleteMapping(DELETE_USER_URL)
	public void deleteUser(HttpServletResponse response, @PathVariable Long id) throws IOException {
		ControllerUtils.performActionThatMayThrowException(response, () -> {
			this.userService.deleteUser(id);
			log.info("User with id {} deleted.", id);
			return null;
		});

		HttpResponse.sendStatus(response, OK);
	}

	@GetMapping(USER_STATS_URL)
	public void getUserStats(HttpServletResponse response, @PathVariable String username) throws IOException {
		HttpResponse.send(response, HttpStatus.OK, this.userService.getUserByUsername(username).getMessageCountByType());
	}
}
