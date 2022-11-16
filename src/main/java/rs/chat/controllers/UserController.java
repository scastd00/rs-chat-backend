package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.User;
import rs.chat.domain.service.SessionService;
import rs.chat.domain.service.UserService;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.smtp.MailSender;
import rs.chat.policies.Policies;

import java.io.IOException;
import java.util.List;

import static rs.chat.router.Routes.GetRoute.OPENED_SESSIONS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.USERS_URL;
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

	/**
	 * Returns all users.
	 *
	 * @param response response containing all users.
	 *
	 * @throws IOException if an error occurs.
	 */
	@GetMapping(USERS_URL)
	public void getUsers(HttpResponse response) throws IOException {
		response.ok().send("data", this.userService.getUsers());
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
	public void saveUser(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject user = (JsonObject) request.body().get("user");

		User savedUser = ControllerUtils.performActionThatMayThrowException(response, () -> {
			Policies.checkRegister(user);

			return this.userService.createUser(
					new User(
							null, // ID
							user.get("username").getAsString(),
							user.get("password").getAsString(),
							user.get("email").getAsString(),
							user.get("fullName").getAsString(),
							null, // Age
							null, // Birthdate
							user.get("role").getAsString(),
							null, // Block until
							null // Password change
					)
			);
		});

		response.created(USER_SAVE_URL).send("data", savedUser);
		MailSender.sendRegistrationEmail(savedUser.getEmail(), savedUser.getUsername());
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
	public void openedSessions(HttpResponse response,
	                           @PathVariable String username) throws IOException {
		List<String> sessionsOfUser = this.sessionService.getSessionsOfUser(username);

		response.ok().send("sessions", sessionsOfUser);
	}
}
