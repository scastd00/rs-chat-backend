package rs.chat.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.Constants;
import rs.chat.config.security.JWTService;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.mappers.SessionMapper;
import rs.chat.domain.entity.mappers.UserMapper;
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
import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.Constants.JWT_TOKEN_PREFIX;
import static rs.chat.Constants.STUDENT_ROLE;
import static rs.chat.router.Routes.PostRoute.CREATE_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.FORGOT_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.LOGOUT_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;

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
	private final Clock clock;
	private final JWTService jwtService;
	private final SessionMapper sessionMapper;
	private final UserMapper userMapper;

	/**
	 * Performs the login of the user.
	 *
	 * @param request the request containing the credentials of the user.
	 * @param res     the response with the user, the session (with JWT token)
	 *                and the chats that the user can access.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(LOGIN_URL)
	public void login(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		String token = request.get("USER:TOKEN").toString();
		String username = request.get("USER:USERNAME").toString();

		boolean isExtendedToken = request.body().get("remember").getAsBoolean();

		// Get all data from db
		User user = ControllerUtils.performActionThatMayThrowException(
				response, () -> this.userService.getUserByUsername(username)
		);

		if (user.getBlockUntil() != null && user.getBlockUntil().isAfter(this.clock.instant())) {
			response.badRequest().send("You are blocked until " + user.getBlockUntil()
			                                                          .atZone(this.clock.getZone())
			                                                          .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			return;
		}

		Session savedSession = this.sessionService.saveSession(
				new Session(
						null,
						request.getRemoteAddr(),
						Instant.now(this.clock),
						Instant.now(this.clock).plus(isExtendedToken ? Constants.TOKEN_EXPIRATION_DURATION_EXTENDED
						                                             : Constants.TOKEN_EXPIRATION_DURATION_NORMAL),
						token,
						user
				)
		);

		JsonObject allChatsOfUserGroupedByType = this.chatService.getAllChatsOfUserGroupedByType(user);

		// Remove the source IP from the session.
		savedSession.setSrcIp(""); // Todo: send it to recognize what session is the user using.

		HttpResponseBody responseBody = new HttpResponseBody("session", this.sessionMapper.toDto(savedSession));
		responseBody.add("user", this.userMapper.toDto(user));
		responseBody.add("chats", allChatsOfUserGroupedByType);

		response.ok().send(responseBody);
	}

	/**
	 * Registers a new user to the application.
	 *
	 * @param request the request containing the credentials of the user.
	 * @param res     the response with the user, the session (with JWT token)
	 *                and the chats that the user can access by registering to the application
	 *                (global group chat by default).
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(REGISTER_URL)
	public void register(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		JsonObject body = request.body();

		Chat globalChat = this.chatService.getByName("Global");
		Group globalGroup = this.groupService.getGroupByName("Global");

		// Check if all the body contains all the necessary fields.
		User savedUser = ControllerUtils.performActionThatMayThrowException(response, () -> {
			Policies.checkRegister(body);

			// Register the user and the session.
			return this.userService.createUser(new User(
					null, // id
					body.get("username").getAsString().trim(), // username
					body.get("password").getAsString().trim(), // password
					body.get("email").getAsString().trim(), // email
					body.get("fullName").getAsString().trim(), // fullName
					null, // age
					null, // birthdate
					STUDENT_ROLE, // role
					null, // blockUntil
					null, // passwordCode
					new JsonObject(), // messageCountByType
					emptySet(), // teacherSubjects
					Set.of(globalGroup), // groups
					emptySet(), // sessions
					emptySet(), // files
					Set.of(globalChat), // chats
					emptySet(), // studentSubjects
					emptySet(), // badges
					emptySet(), // friends
					emptySet(), // blockedUsers
					(byte) 0 // nsfwCount
			));
		});

		// Generate tokens
		String token = this.jwtService.generateToken(
				savedUser.getUsername(),
				request.getRequestURL().toString(),
				savedUser.getRole(),
				false
		);

		Session session = this.sessionService.saveSession(
				new Session(
						null,
						request.getRemoteAddr(),
						Instant.now(this.clock),
						Instant.now(this.clock).plus(Constants.TOKEN_EXPIRATION_DURATION_NORMAL),
						token,
						savedUser
				)
		);

		JsonObject defaultChat = new JsonObject();
		JsonObject chatJson = new JsonObject();
		chatJson.addProperty("id", globalChat.getId());
		chatJson.addProperty("name", globalChat.getName());
		JsonArray value = new JsonArray();
		value.add(chatJson);
		defaultChat.add(globalChat.getType(), value);

		// Clear the source IP of the session
		session.setSrcIp("");

		HttpResponseBody responseBody = new HttpResponseBody("session", this.sessionMapper.toDto(session));
		responseBody.add("user", this.userMapper.toDto(savedUser));
		responseBody.add("chats", defaultChat);

		response.ok().send(responseBody);
		MailSender.sendRegistrationEmailBackground(savedUser.getEmail(), savedUser.getUsername());
	}

	/**
	 * Performs the logout of the user.
	 *
	 * @param request the request containing the token to be deleted.
	 * @param res     OK response if the token is deleted.
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(LOGOUT_URL)
	public void logout(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		String token = request.getHeader(AUTHORIZATION);

		// Todo: this is not reached, since the filter is executed before this method, and it throws an exception.
		//  Check if the token ignoring the prefix is ok.
		//! From here
//		if (token == null) {
//			// If request does not contain authorization header, send error.
//			response.badRequest().send("You must provide the authorization token");
//			log.warn("Request does not contain authorization header");
//			return;
//		}

		String tokenWithoutPrefix = token.substring(JWT_TOKEN_PREFIX.length());

		if (this.jwtService.isInvalidToken(token)) {
			response.badRequest().send("The token is not valid");
			log.warn("The token is not valid");
			this.sessionService.deleteSession(tokenWithoutPrefix);
			return;
		}
		//! Up to here, the code is not reached. Because the filter checks if the token is valid.

		String username = this.jwtService.getUsername(token);

		// Check if we must delete all the user sessions or only one.
		if (request.body().get("fromAllSessions").getAsBoolean()) {
			log.info("Deleting all the sessions of the user {}", username);
			this.sessionService.deleteAllSessionsOfUser(username);
		} else {
			log.info("User {} has been logged out from the session", username);
			this.sessionService.deleteSession(tokenWithoutPrefix);
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
	 * @param request the request with the email of the user.
	 * @param res     the response (only status code is sent if successful).
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(FORGOT_PASSWORD_URL)
	public void forgotPassword(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		JsonObject body = request.body();
		String email = body.get("email").getAsString();

		User user = ControllerUtils.performActionThatMayThrowException(response, () -> {
			// Check if the email is correct.
			Policies.checkEmail(body);

			return this.userService.getUserByEmail(email);
		});

		if (user.getBlockUntil() != null && user.getBlockUntil().isAfter(Instant.now(this.clock))) {
			response.badRequest().send("You are blocked until " + user.getBlockUntil()
			                                                          .atZone(this.clock.getZone())
			                                                          .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			return;
		}

		String code = RandomStringUtils.randomAlphanumeric(6);
		user.setPasswordCode(code);
		this.userService.saveUser(user);

		response.sendStatus(OK);
		MailSender.sendResetPasswordEmailBackground(email, code);
	}

	/**
	 * Creates a new password for the user.
	 *
	 * @param request the request with the new password.
	 * @param res     the response (only status code is sent if successful).
	 *
	 * @throws IOException if an error occurs.
	 */
	@PostMapping(CREATE_PASSWORD_URL)
	public void createPassword(HttpRequest request, HttpServletResponse res) throws IOException {
		HttpResponse response = new HttpResponse(res);
		JsonObject body = request.body();
		String code = body.get("code").getAsString();

		// Check if the code exists
		User user = ControllerUtils.performActionThatMayThrowException(response, () -> {
			// Check if the passwords are correct.
			Policies.checkPasswords(body);

			return this.userService.getUserByCode(code);
		});

		user.setPassword(body.get("password").getAsString());
		user.setPasswordCode(null); // Remove the password code.
		this.userService.changePassword(user);

		response.sendStatus(OK);
	}
}
