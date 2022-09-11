package rs.chat.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.mail.MailSender;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.policies.Policies;
import rs.chat.service.ChatService;
import rs.chat.service.GroupService;
import rs.chat.service.SessionService;
import rs.chat.service.UserGroupService;
import rs.chat.service.UserService;
import rs.chat.utils.Constants;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.PostRoute.CREATE_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.FORGOT_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.LOGOUT_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;
import static rs.chat.utils.Constants.ERROR_JSON_KEY;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static rs.chat.utils.Constants.JWT_VERIFIER;
import static rs.chat.utils.Constants.STUDENT_ROLE;
import static rs.chat.utils.Utils.parseJson;

/**
 * Controller that manages all credential-related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
	private final UserService userService;
	private final SessionService sessionService;
	private final ChatService chatService;
	private final GroupService groupService;
	private final UserGroupService userGroupService;

	/**
	 * Performs the login of the user.
	 *
	 * @param request  the request containing the credentials of the user.
	 * @param response the response with the user, the session (with access and refresh tokens)
	 *                 and the chats that the user can access.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(LOGIN_URL)
	public void login(HttpRequest request, HttpResponse response) throws IOException {
		String jsonTokens = request.get("USER:TOKENS").toString();
		String username = request.get("USER:USERNAME").toString();

		JsonObject tokens = parseJson(jsonTokens);

		// Get all data from db
		User user = this.userService.getUser(username);
		Session savedSession = this.sessionService.saveSession(
				new Session(
						null,
						request.getRemoteAddr(),
						Instant.now(), // Todo: pass a clock as parameter to test better
						tokens.get("accessToken").getAsString(),
						tokens.get("refreshToken").getAsString(),
						user.getId()
				)
		);

		Map<String, List<Map<String, Object>>> allChatsOfUserGroupedByType = this.chatService.getAllChatsOfUserGroupedByType(user.getId());

		// Clear sensitive data
		user.setPassword(null); // Password not visible in the response
		savedSession.setSrcIp(null);

		HttpResponseBody responseBody = new HttpResponseBody("session", savedSession);
		responseBody.add("user", user);
		responseBody.add("chats", allChatsOfUserGroupedByType);

		response.ok().send(responseBody);
	}

	/**
	 * Registers a new user to the application.
	 *
	 * @param request  the request containing the credentials of the user.
	 * @param response the response with the user, the session (with access and refresh tokens)
	 *                 and the chats that the user can access by registering to the application
	 *                 (global group chat by default).
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(REGISTER_URL)
	public void register(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();

		// Check if all the body contains all the necessary fields.
		Policies.checkRegister(body);

		// Register the user and the session.
		User savedUser = this.userService.saveUser(new User(
				null,
				body.get("username").getAsString().trim(),
				body.get("password").getAsString().trim(),
				body.get("email").getAsString().trim(),
				body.get("fullName").getAsString().trim(),
				null,
				null,
				STUDENT_ROLE,
				null,
				null
		));

		// Generate tokens
		Map<String, String> tokens = Utils.generateTokens(
				savedUser.getUsername(),
				request.getRequestURL().toString(),
				savedUser.getRole(),
				false
		);

		Session session = this.sessionService.saveSession(
				new Session(
						null,
						request.getRemoteAddr(),
						Instant.now(), // Todo: pass a clock as parameter to test better
						tokens.get("accessToken"),
						tokens.get("refreshToken"),
						savedUser.getId()
				)
		);

		Chat globalChat = this.chatService.getByName("Global");
		Group globalGroup = this.groupService.getGroupByName("Global");
		Long userId = savedUser.getId();

		this.userGroupService.addUserToGroup(userId, globalGroup.getId());
		this.chatService.addUserToChat(userId, globalChat.getId());

		// Make the Map of the only available chat without calling database
		Map<String, List<Map<String, Object>>> defaultChat =
				Map.of(
						globalChat.getType(),
						List.of(
								Map.of(
										"id", globalChat.getId(),
										"name", globalChat.getName()
								)
						)
				);

		// Clear the password
		savedUser.setPassword(null);
		session.setSrcIp(null);

		HttpResponseBody responseBody = new HttpResponseBody("session", session);
		responseBody.add("user", savedUser);
		responseBody.add("chats", defaultChat);

		response.ok().send(responseBody);
		MailSender.sendRegistrationEmail(savedUser.getEmail(), savedUser.getUsername());
	}

	/**
	 * Performs the logout of the user.
	 *
	 * @param request  the request containing the token to be deleted.
	 * @param response OK response if the token is deleted.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(LOGOUT_URL)
	public void logout(HttpRequest request, HttpResponse response) throws IOException {
		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader == null) {
			// If request does not contain authorization header send error.
			response.status(BAD_REQUEST).send(ERROR_JSON_KEY, "You must provide the authorization token");
			return;
		}

		String token = authorizationHeader.substring(JWT_TOKEN_PREFIX.length());
		DecodedJWT decodedJWT = JWT_VERIFIER.verify(token);
		String username = decodedJWT.getSubject();

		log.info("User {} has been logged out", username);

		String body = IOUtils.toString(request.getReader());
		JsonObject jsonBody = Constants.GSON.fromJson(body, JsonObject.class);

		this.sessionService.deleteSession(token); // Always delete the session.

		// And then check if we must delete all the other sessions.
		if (jsonBody.has("deleteAllSessions")) {
			this.sessionService.deleteAllSessionsOfUser(username);
		}

		// Logout from Spring.
		new SecurityContextLogoutHandler().logout(request, null, null);
		response.sendStatus(OK);
	}

	/**
	 * Sends an email to the user with the code to reset the password.
	 * This method can be used when a user wants to change the password inside
	 * the profile (first forget password, then create password).
	 *
	 * @param request  the request with the email of the user.
	 * @param response the response (only status code is sent if successful).
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(FORGOT_PASSWORD_URL)
	public void forgotPassword(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();

		// Check if the email is correct.
		Policies.checkEmail(body);

		String email = body.get("email").getAsString();
		User user = this.userService.getUserByEmail(email);

		if (user == null) {
			response.status(BAD_REQUEST).send(ERROR_JSON_KEY, "The email is not registered");
			return;
		}

		String code = RandomStringUtils.randomAlphanumeric(6);

		user.setPasswordCode(code);
		this.userService.saveUser(user);

		response.sendStatus(OK);

		MailSender.resetPassword(email, code);
	}

	/**
	 * Creates a new password for the user.
	 *
	 * @param request  the request with the new password.
	 * @param response the response (only status code is sent if successful).
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(CREATE_PASSWORD_URL)
	public void createPassword(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();
		String code = body.get("code").getAsString();

		// Check if the code exists
		User user = this.userService.getUserByCode(code);

		if (user == null) {
			response.status(BAD_REQUEST).send(ERROR_JSON_KEY, "The code is not valid");
			return;
		}

		// Check if the passwords are correct.
		Policies.checkPasswords(body);

		user.setPassword(body.get("newPassword").getAsString());
		user.setPasswordCode(null);
		this.userService.saveUser(user);

		response.sendStatus(OK);
	}
}
