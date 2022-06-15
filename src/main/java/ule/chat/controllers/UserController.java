package ule.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ule.chat.domain.User;
import ule.chat.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
	private final UserService userService;

	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers() {
		return ResponseEntity.ok(this.userService.getUsers());
	}

	@PostMapping("/user/save")
	public ResponseEntity<User> saveUser(@RequestBody User user) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
		                                                .path("/api/user/save")
		                                                .toUriString());
		return ResponseEntity.created(uri)
		                     .body(this.userService.saveUser(user));
	}

	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
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
