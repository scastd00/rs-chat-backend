package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.chat.domain.User;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.router.Routes;
import rs.chat.service.SessionService;
import rs.chat.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final SessionService sessionService;

	@GetMapping(Routes.USERS_URL)
	public ResponseEntity<List<User>> getUsers() {
		return ResponseEntity.ok(this.userService.getUsers());
	}

	@PostMapping(Routes.USER_SAVE_URL)
	public ResponseEntity<User> saveUser(@RequestBody User user) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
		                                                .path(Routes.USER_SAVE_URL)
		                                                .toUriString());
		return ResponseEntity.created(uri)
		                     .body(this.userService.saveUser(user));
	}

	@GetMapping(Routes.REFRESH_TOKEN_URL)
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

	@GetMapping(Routes.OPENED_SESSIONS_URL)
	public void openedSessions(HttpRequest request,
	                           HttpResponse response,
	                           @PathVariable String username) throws IOException {
		List<String> sessionsOfUser = this.sessionService.getSessionsOfUser(username);

		response.status(OK).send("sessions", sessionsOfUser);
	}
}
