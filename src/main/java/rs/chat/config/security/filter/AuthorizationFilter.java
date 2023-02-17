package rs.chat.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.chat.config.security.JWTService;
import rs.chat.domain.entity.Session;
import rs.chat.domain.service.SessionService;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static rs.chat.router.Routes.ACTUATOR_URL;
import static rs.chat.router.Routes.GetRoute.HEALTH_URL;
import static rs.chat.router.Routes.PostRoute.CREATE_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.FORGOT_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;
import static rs.chat.router.Routes.TEST_URL;
import static rs.chat.router.Routes.WS_CHAT_ENDPOINT;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;

/**
 * Filter that checks if the user is authorized to access the resource.
 */
@Slf4j
@WebFilter(filterName = "AuthorizationFilter")
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
	private final JWTService jwtService;
	private final SessionService sessionService;

	/**
	 * Checks if the user is authorized to access the resource, sets the authentication
	 * and passes the request to the next filter.
	 *
	 * @param request  HTTP request.
	 * @param response HTTP response.
	 * @param chain    the filter chain.
	 *
	 * @throws IOException      if an I/O error occurs.
	 * @throws ServletException if a servlet error occurs.
	 */
	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request,
	                                @NotNull HttpServletResponse response,
	                                @NotNull FilterChain chain) throws ServletException, IOException {
		if (this.isUnknownPath(request.getServletPath())) {
			HttpResponse.sendStatus(response, NOT_FOUND);
			return;
		}

		if (this.isExcludedPath(request.getServletPath())) {
			chain.doFilter(request, response);
			return;
		}

		String token = request.getHeader(AUTHORIZATION);

		if (token == null || !token.startsWith(JWT_TOKEN_PREFIX)) {
			// All routes need the JWT token except for the routes excluded above.
			log.error("Authorization header is missing or invalid.");
			throw new ServletException("Missing or invalid Authorization header.");
			// Todo: see the video
		}

		try {
			// The session could not exist if the user has logged out from all sessions (so this
			// method will throw an exception).
			Session session = this.sessionService.getSessionByToken(token.substring(JWT_TOKEN_PREFIX.length()));
			if (this.sessionService.isExpiredSession(session)) {
				throw new ServletException("Your session is expired.");
			}

			String username = this.jwtService.getUsername(token);
			String role = this.jwtService.getClaim(token, claims -> claims.get("role", String.class));

			SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(
							username, null, Collections.singleton(authority)
					);

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			HttpResponse.send(response, HttpStatus.FORBIDDEN, e.getMessage());
		}
	}

	/**
	 * Checks if the path is excluded from the authorization filter.
	 *
	 * @param path the path to check.
	 *
	 * @return {@code true} if the path is excluded, {@code false} otherwise.
	 */
	private boolean isExcludedPath(String path) {
		return path.equals(LOGIN_URL) || path.equals(REGISTER_URL) ||
				path.equals(WS_CHAT_ENDPOINT) || path.equals(FORGOT_PASSWORD_URL) ||
				path.equals(CREATE_PASSWORD_URL) || path.equals(HEALTH_URL) ||
				path.equals(ACTUATOR_URL) || path.equals(TEST_URL);
	}

	/**
	 * Checks if the path is unknown.
	 *
	 * @param path the path to check.
	 *
	 * @return {@code true} if the path is unknown, {@code false} otherwise.
	 */
	private boolean isUnknownPath(String path) {
		return path.equals("/favicon.ico");
	}
}
