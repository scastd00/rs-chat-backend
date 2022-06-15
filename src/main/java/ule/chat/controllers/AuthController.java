package ule.chat.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ule.chat.domain.Session;
import ule.chat.service.SessionService;
import ule.chat.service.UserService;
import ule.chat.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ule.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static ule.chat.utils.Constants.JWT_VERIFIER;
import static ule.chat.utils.Utils.typeToken;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
	private final UserService userService;
	private final SessionService sessionService;

	@PostMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Object jsonTokens = request.getAttribute("USER:TOKENS");
		String username = (String) request.getAttribute("USER:USERNAME");

		request.removeAttribute("USER:TOKENS");
		request.removeAttribute("USER:USERNAME");

		Map<String, String> tokenMap = Constants.GSON.fromJson(jsonTokens.toString(), typeToken());
		String accessToken = tokenMap.get("access_token");
		String refreshToken = tokenMap.get("refresh_token");

		Session savedSession = this.sessionService.saveSession(
				new Session(
						null,
						request.getRemoteAddr(),
						Timestamp.from(Instant.now()),
						accessToken,
						refreshToken,
						this.userService.getUser(username).getId()
				)
		);

//		response.setStatus(HttpStatus.CONTINUE.value());
		response.setContentType(APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getWriter(), savedSession);
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request) {
		// Todo: check if we must delete all sessions. If only one session is requested,
		//  we take the token, and we delete the session. If more than one session is
		//  requested, we take the username, and we delete all the sessions.
		String token = request.getHeader(AUTHORIZATION).substring(JWT_TOKEN_PREFIX.length());
		DecodedJWT decodedJWT = JWT_VERIFIER.verify(token);
		String username = decodedJWT.getSubject();
		request.setAttribute("USER:LOGOUT", username);

		log.info("User {} has been logged out", username);
		this.sessionService.deleteAllSessionsOfUser(username);
	}
}
