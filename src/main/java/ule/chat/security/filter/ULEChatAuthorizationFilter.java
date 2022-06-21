package ule.chat.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ule.chat.router.Routes.LOGIN;
import static ule.chat.router.Routes.REFRESH_TOKEN;
import static ule.chat.router.Routes.REGISTER;
import static ule.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static ule.chat.utils.Constants.JWT_VERIFIER;

@Slf4j
@WebFilter(filterName = "AuthorizationFilter")
public class ULEChatAuthorizationFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request,
	                                @NotNull HttpServletResponse response,
	                                @NotNull FilterChain filterChain) throws ServletException, IOException {
		if (this.isExcludedPath(request.getServletPath())) {
			filterChain.doFilter(request, response);
			return;
		}

		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith(JWT_TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
		} else {
			try {
				String token = authorizationHeader.substring(JWT_TOKEN_PREFIX.length());
				DecodedJWT decodedJWT = JWT_VERIFIER.verify(token);
				String username = decodedJWT.getSubject();
				String role = decodedJWT.getClaim("role").asString();

				log.info("User {} with role {} has been authorized", username, role);

				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(
								username, null, Collections.singleton(authority)
						);

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				filterChain.doFilter(request, response);
			} catch (Exception e) {
				response.setStatus(FORBIDDEN.value());

				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());

				response.setContentType(APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getWriter(), error);
			}
		}
	}

	private boolean isExcludedPath(String path) {
		return path.equals(LOGIN.url()) ||
				path.equals(REFRESH_TOKEN.url()) ||
				path.equals(REGISTER.url());
	}
}