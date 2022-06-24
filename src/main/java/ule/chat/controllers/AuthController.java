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
import ule.chat.net.HttpRequest;
import ule.chat.net.HttpResponse;
import ule.chat.router.Routes;
import ule.chat.service.SessionService;
import ule.chat.service.UserService;
import ule.chat.utils.Constants;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ule.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static ule.chat.utils.Constants.JWT_VERIFIER;
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
						Timestamp.from(Instant.now()),
						tokens.get("access_token").getAsString(),
						tokens.get("refresh_token").getAsString(),
						this.userService.getUser(username).getId()
				)
		);

		response.status(HttpStatus.OK).send(savedSession);
	}

	@PostMapping(Routes.REGISTER_URL)
	public void register(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject body = request.body();

		if (!body.has("agreeTerms") || !body.get("agreeTerms").getAsBoolean()) {
			response.status(BAD_REQUEST).send("You must accept the terms and conditions.");
			return;
		}

		// Todo: check all the conditions
		String username = body.get("username").getAsString();


		// Register the session

		// Generate tokens

		response.sendStatus(OK);
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
