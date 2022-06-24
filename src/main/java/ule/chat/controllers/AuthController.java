package ule.chat.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ule.chat.domain.Session;
import ule.chat.domain.User;
import ule.chat.net.HttpRequest;
import ule.chat.net.HttpResponse;
import ule.chat.policies.Policies;
import ule.chat.router.Routes;
import ule.chat.service.SessionService;
import ule.chat.service.UserService;
import ule.chat.utils.Constants;
import ule.chat.utils.Utils;

import java.io.IOException;
import java.time.Instant;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ule.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static ule.chat.utils.Constants.JWT_VERIFIER;
import static ule.chat.utils.Constants.STUDENT_ROLE;
import static ule.chat.utils.Utils.readJson;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
	private final UserService userService;
	private final SessionService sessionService;

	@PostMapping(Routes.LOGIN_URL)
	public void login(HttpRequest request, HttpResponse response) throws IOException {
		String jsonTokens = request.get("USER:TOKENS").toString();
		String username = (String) request.get("USER:USERNAME");

		JsonObject tokens = readJson(jsonTokens);

		Session savedSession = this.sessionService.saveSession(
				new Session(
						null,
						request.getRemoteAddr(),
						Instant.now(), // Todo: pass a clock as parameter to test better
						tokens.get("accessToken").getAsString(),
						tokens.get("refreshToken").getAsString(),
						this.userService.getUser(username)
				)
		);

		response.status(HttpStatus.OK).send(savedSession);
	}

	@PostMapping(Routes.REGISTER_URL)
	public void register(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();

		// Check if all the body contains all the necessary fields.
		Policies.checkRegister(body);

		// Register the user and the session.
		User user = new User(
				null,
				body.get("username").getAsString(),
				body.get("password").getAsString(),
				body.get("email").getAsString(),
				body.get("fullName").getAsString(),
				null,
				null,
				STUDENT_ROLE,
				null
		);
		this.userService.saveUser(user);

		// Generate tokens

		response.status(OK)
		        .send(Utils.generateTokens(user.getUsername(), request, user.getRole()));
	}

	@PostMapping(Routes.LOGOUT_URL)
	public void logout(HttpRequest request, HttpResponse response) throws IOException {
		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader == null) { // If request does not contain authorization header send error.
			response.setStatus(BAD_REQUEST.value());
			response.setContentType(APPLICATION_JSON_VALUE);
			new ObjectMapper().writeValue(response.getWriter(), "You must provide the authorization token");
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
	}
}
