package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.utils.Constants.ALGORITHM;

public final class Utils {
	private Utils() {
	}

	public static JsonObject readJson(String jsonString) {
		// @formatter:off
		return Constants.GSON.fromJson(jsonString, new TypeToken<JsonObject>() {}.getType());
		// @formatter:on
	}

	public static Map<String, String> generateTokens(String username, HttpServletRequest request, String role) {
		Map<String, String> tokens = new HashMap<>();

		String accessToken = JWT.create()
		                        .withSubject(username)
		                        .withExpiresAt(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME))
		                        .withIssuer(request.getRequestURL().toString()) // URL of our application.
		                        .withClaim("role", role) // Only one role is in DB.
		                        .sign(ALGORITHM);

		String refreshToken = JWT.create()
		                         .withSubject(username)
		                         .withExpiresAt(new Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_EXPIRATION_TIME))
		                         .withIssuer(request.getRequestURL().toString()) // URL of our application.
		                         .sign(ALGORITHM);

		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);

		return tokens;
	}
}
