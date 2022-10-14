package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.User;
import rs.chat.mail.MailSender;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.SessionService;
import rs.chat.service.UserService;

import java.io.IOException;
import java.util.List;

import static rs.chat.router.Routes.GetRoute.OPENED_SESSIONS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.USERS_URL;
import static rs.chat.router.Routes.PostRoute.USER_SAVE_URL;
import static rs.chat.router.Routes.REFRESH_TOKEN_URL;
import static rs.chat.utils.Constants.STUDENT_ROLE;

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

		User savedUser = this.userService.createUser(
				new User(
						null, // ID
						user.get("username").getAsString(),
						user.get("password").getAsString(),
						user.get("email").getAsString(),
						user.get("fullName").getAsString(),
						null, // Age
						null, // Birthdate
						STUDENT_ROLE,
						null, // Block until
						null // Password change
				)
		);

		response.created(USER_SAVE_URL).send("data", savedUser);
		MailSender.sendRegistrationEmail(savedUser.getEmail(), savedUser.getUsername());
	}

	@GetMapping(REFRESH_TOKEN_URL)
	public void refreshToken(HttpRequest request, HttpResponse response) {
//		String authorizationHeader = request.getHeader(AUTHORIZATION);
//
//		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//			throw new RuntimeException("Refresh token is missing");
//		} else {
//			try {
//				String token = authorizationHeader.substring("Bearer ".length());
//				JWTVerifier verifier = JWT.require(ALGORITHM).build();
//				DecodedJWT decodedJWT = verifier.verify(token);
//				String username = decodedJWT.getSubject();
//				User user = this.userService.getUser(username);
//				String role = decodedJWT.getClaim("role").asString();
//				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
//				UsernamePasswordAuthenticationToken authenticationToken =
//						new UsernamePasswordAuthenticationToken(
//								username, null, Collections.singleton(authority)
//						);
//				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//			} catch (Exception e) {
//				response.setHeader("error", e.getMessage());
//				response.status(FORBIDDEN.value());
//
//				Map<String, String> tokens = new HashMap<>();
//				tokens.put("error_message", e.getMessage());
//
//				response.setContentType(APPLICATION_JSON_VALUE);
//				new ObjectMapper().writeValue(response.getWriter(), tokens);
//			}
//		}
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
