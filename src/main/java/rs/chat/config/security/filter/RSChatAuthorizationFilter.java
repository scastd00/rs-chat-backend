package rs.chat.config.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.chat.net.http.HttpResponse;
import rs.chat.utils.Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;
import static rs.chat.router.Routes.REFRESH_TOKEN_URL;
import static rs.chat.utils.Constants.ERROR_JSON_KEY;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;

/**
 * Filter that checks if the user is authorized to access the resource.
 */
@Slf4j
@WebFilter(filterName = "AuthorizationFilter")
public class RSChatAuthorizationFilter extends OncePerRequestFilter {
	/**
	 * Checks if the user is authorized to access the resource.
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
		if (this.isExcludedPath(request.getServletPath())) {
			chain.doFilter(request, response);
			return;
		}

		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith(JWT_TOKEN_PREFIX)) {
			// Todo: what is this case. Login, register and others are checked in the previous if ???
			chain.doFilter(request, response);
		} else {
			try {
				DecodedJWT decodedJWT = Utils.checkAuthorizationToken(authorizationHeader);
				String username = decodedJWT.getSubject();
				String role = decodedJWT.getClaim("role").asString();

				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(
								username, null, Collections.singleton(authority)
						);

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				chain.doFilter(request, response);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				new HttpResponse(response).status(FORBIDDEN)
				                          .send(ERROR_JSON_KEY, e.getMessage());
			}
		}
	}

	private boolean isExcludedPath(String path) {
		return path.equals(LOGIN_URL) ||
				path.equals(REFRESH_TOKEN_URL) ||
				path.equals(REGISTER_URL);
	}
}
