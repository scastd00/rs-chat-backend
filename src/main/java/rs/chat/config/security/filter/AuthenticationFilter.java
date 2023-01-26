package rs.chat.config.security.filter;

import com.google.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.chat.exceptions.CouldNotAuthenticateException;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.time.Clock;

/**
 * Manager that authenticates the incoming requests.
 */
@Slf4j
@WebFilter(filterName = "AuthenticationFilter")
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final Clock clock;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			HttpRequest req = new HttpRequest(request);
			JsonObject jsonBody = req.body();

			String username = jsonBody.get("username").getAsString();
			String password = jsonBody.get("password").getAsString();

			// We can take the info we need from the request after passing it into an ObjectMapper.
			// request.getHeader("Authorization")
			return this.authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password)
			);
		} catch (IOException e) {
			log.error("Could not authenticate request", e);
			throw new CouldNotAuthenticateException(e.getMessage());
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
	                                          HttpServletResponse response,
	                                          AuthenticationException failed) throws IOException {
		log.warn("Authentication failed", failed);
		new HttpResponse(response)
				.badRequest() // Since the check for the user is only done one time, the exceptional case is that the user is not registered.
				.send(failed.getMessage());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
	                                        HttpServletResponse response,
	                                        FilterChain chain,
	                                        Authentication authentication) throws IOException, ServletException {
		HttpRequest req = new HttpRequest(request);
		User user = (User) authentication.getPrincipal();

		String token = Utils.generateJWTToken(
				user.getUsername(),
				request.getRequestURL().toString(),
				user.getAuthorities()
				    .iterator()
				    .next() // We only have one role per user, so we take it.
				    .getAuthority(),
				req.body().get("remember").getAsBoolean(),
				this.clock
		);

		req.set("USER:TOKEN", token);
		req.set("USER:USERNAME", user.getUsername());

		//! IMPORTANT: this enables calling a Controller after the token is created.
		//! In order to call the controller, we need to add the token to a request attribute.
		chain.doFilter(req, response);
	}
}
