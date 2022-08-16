package rs.chat.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.User;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.net.http.HttpResponse.HttpResponseBody;
import rs.chat.policies.Policies;
import rs.chat.service.ChatService;
import rs.chat.service.SessionService;
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
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.LOGOUT_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;
import static rs.chat.router.Routes.PutRoute.CHANGE_PASSWORD_URL;
import static rs.chat.utils.Constants.ERROR_JSON_KEY;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static rs.chat.utils.Constants.JWT_VERIFIER;
import static rs.chat.utils.Constants.STUDENT_ROLE;
import static rs.chat.utils.Utils.parseJson;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
	private final UserService userService;
	private final SessionService sessionService;
	private final ChatService chatService;

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

	@PostMapping(REGISTER_URL)
	public void register(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();

		// Check if all the body contains all the necessary fields.
		Policies.checkRegister(body);

		// Register the user and the session.
		User user = this.userService.saveUser(new User(
				null,
				body.get("username").getAsString(),
				body.get("password").getAsString(),
				body.get("email").getAsString(),
				body.get("fullName").getAsString(),
				null,
				null,
				STUDENT_ROLE,
				null
		));

		// Generate tokens
		Map<String, String> tokens = Utils.generateTokens(user.getUsername(),
		                                                  request.getRequestURL().toString(),
		                                                  user.getRole());

		Session session = this.sessionService.saveSession(
				new Session(
						null,
						request.getRemoteAddr(),
						Instant.now(), // Todo: pass a clock as parameter to test better
						tokens.get("accessToken"),
						tokens.get("refreshToken"),
						user.getId()
				)
		);

		Chat globalChat = this.chatService.getByName("global");
		this.chatService.addUserToChat(user.getId(), globalChat.getId());

		// Make the Map of available chat directly without calling db
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
		user.setPassword(null);
		session.setSrcIp(null);

		HttpResponseBody responseBody = new HttpResponseBody("session", session);
		responseBody.add("user", user);
		responseBody.add("chats", defaultChat);

		// Todo: write chat like when sending all available to user

		response.ok().send(responseBody);
	}

	@PostMapping(LOGOUT_URL)
	public void logout(HttpRequest request, HttpResponse response) throws IOException {
		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader == null) { // If request does not contain authorization header send error.
			response.status(BAD_REQUEST)
			        .send(ERROR_JSON_KEY, "You must provide the authorization token");
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

	@PutMapping(CHANGE_PASSWORD_URL)
	public void changePassword(HttpRequest request,
	                           HttpResponse response,
	                           @PathVariable String username) throws IOException {
		JsonObject body = request.body();

		// Check if both passwords are correct.
		Policies.checkPasswords(body);

		// We can update the session or keep the same and send the new one when
		// the user enters the page the next time.

		User user = this.userService.getUser(username);
		user.setPassword(body.get("newPassword").getAsString());
		this.userService.saveUser(user);

		response.sendStatus(OK);
	}
}
