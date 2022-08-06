package rs.chat.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.chat.domain.User;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.service.SessionService;
import rs.chat.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static rs.chat.router.Routes.GetRoute.OPENED_SESSIONS_OF_USER_URL;
import static rs.chat.router.Routes.GetRoute.USERS_URL;
import static rs.chat.router.Routes.PostRoute.USER_SAVE_URL;
import static rs.chat.router.Routes.REFRESH_TOKEN_URL;
import static rs.chat.utils.Constants.STUDENT_ROLE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final SessionService sessionService;

	@GetMapping(USERS_URL)
	public void getUsers(HttpResponse response) throws IOException {
		response.status(OK).send("data", this.userService.getUsers());
	}

	@PostMapping(USER_SAVE_URL)
	public void saveUser(HttpRequest request, HttpResponse response) throws IOException {
		JsonObject user = (JsonObject) request.body().get("user");

		User savedUser = this.userService.saveUser(
				new User(
						null, // ID
						user.get("username").getAsString(),
						user.get("password").getAsString(),
						user.get("email").getAsString(),
						user.get("fullName").getAsString(),
						null, // Age
						null, // Birthdate
						STUDENT_ROLE,
						null // Block until
				)
		);

		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
		                                                .path(USER_SAVE_URL)
		                                                .toUriString());
		response.setHeader("Location", uri.toString());
		response.status(CREATED).send("data", savedUser);
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

	@GetMapping(OPENED_SESSIONS_OF_USER_URL)
	public void openedSessions(HttpResponse response,
	                           @PathVariable String username) throws IOException {
		List<String> sessionsOfUser = this.sessionService.getSessionsOfUser(username);

		response.status(OK).send("sessions", sessionsOfUser);
	}
}
