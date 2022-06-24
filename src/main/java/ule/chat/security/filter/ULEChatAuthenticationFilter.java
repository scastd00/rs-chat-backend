package ule.chat.security.filter;

import com.auth0.jwt.JWT;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ule.chat.utils.Constants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ule.chat.utils.Constants.ALGORITHM;

@Slf4j
@WebFilter(filterName = "AuthenticationFilter")
public class ULEChatAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;

	public ULEChatAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			String body = IOUtils.toString(request.getReader());
			JsonObject jsonBody = Constants.GSON.fromJson(body, JsonObject.class);

			String username = jsonBody.get("username").getAsString();
			String password = jsonBody.get("password").getAsString();

			UsernamePasswordAuthenticationToken token =
					new UsernamePasswordAuthenticationToken(username, password);
			// We can take the info we need from the request after passing it into an ObjectMapper.
			// request.getHeader("Authorization")
			return this.authenticationManager.authenticate(token);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
	                                        HttpServletResponse response,
	                                        FilterChain chain,
	                                        Authentication authentication) throws IOException, ServletException {
		User user = (User) authentication.getPrincipal();
		String accessToken = JWT.create()
		                        .withSubject(user.getUsername())
		                        .withExpiresAt(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME))
		                        .withIssuer(request.getRequestURL().toString()) // URL of our application.
		                        .withClaim("role", user.getAuthorities().iterator().next().getAuthority()) // Only one role is in DB.
		                        .sign(ALGORITHM);

		String refreshToken = JWT.create()
		                         .withSubject(user.getUsername())
		                         .withExpiresAt(new Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_EXPIRATION_TIME))
		                         .withIssuer(request.getRequestURL().toString()) // URL of our application.
		                         .sign(ALGORITHM);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("access_token", accessToken);
		tokens.put("refresh_token", refreshToken);

		//! In the filters we must use the methods provided in the interfaces.
		//! In the controllers we can cast to our HttpRequest without throwing exception
		request.setAttribute("USER:TOKENS", Constants.GSON.toJson(tokens));
		request.setAttribute("USER:USERNAME", user.getUsername());

		//! IMPORTANT: this enables calling the controller after the token is created
		//! in order to call the controller, we need to add the token to a request attribute.
		chain.doFilter(request, response);
	}
}
