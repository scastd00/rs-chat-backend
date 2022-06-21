package ule.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ule.chat.domain.User;
import ule.chat.net.HttpRequest;
import ule.chat.net.HttpResponse;
import ule.chat.router.Routes;
import ule.chat.service.UserService;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

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
//				response.setStatus(FORBIDDEN.value());
//
//				Map<String, String> tokens = new HashMap<>();
//				tokens.put("error_message", e.getMessage());
//
//				response.setContentType(APPLICATION_JSON_VALUE);
//				new ObjectMapper().writeValue(response.getWriter(), tokens);
//			}
//		}
	}
}
