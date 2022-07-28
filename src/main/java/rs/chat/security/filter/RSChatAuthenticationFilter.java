package rs.chat.security.filter;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.chat.exceptions.CouldNotAuthenticate;
import rs.chat.net.http.HttpRequest;
import rs.chat.utils.Constants;
import rs.chat.utils.Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@WebFilter(filterName = "AuthenticationFilter")
public class RSChatAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;

	public RSChatAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

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
			throw new CouldNotAuthenticate(e.getMessage());
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
	                                        HttpServletResponse response,
	                                        FilterChain chain,
	                                        Authentication authentication) throws IOException, ServletException {
		User user = (User) authentication.getPrincipal();
		Map<String, String> tokens = Utils.generateTokens(user.getUsername(),
		                                                  request.getRequestURL().toString(),
		                                                  user.getAuthorities()
		                                                      .iterator()
		                                                      .next()
		                                                      .getAuthority());

		HttpRequest req = new HttpRequest(request);
		req.set("USER:TOKENS", Constants.GSON.toJson(tokens));
		req.set("USER:USERNAME", user.getUsername());

		//! IMPORTANT: this enables calling a Controller after the token is created.
		//! In order to call the controller, we need to add the token to a request attribute.
		chain.doFilter(req, response);
	}
}
