package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import rs.chat.net.ws.JsonMessageWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.ERROR_MESSAGE;
import static rs.chat.utils.Constants.ALGORITHM;
import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static rs.chat.utils.Constants.JWT_VERIFIER;

public final class Utils {
	private Utils() {
	}

	/**
	 * Parses a JSON string into a {@link JsonObject}.
	 *
	 * @param jsonString
	 *
	 * @return
	 */
	public static JsonObject parseJson(String jsonString) {
		// @formatter:off
		return GSON.fromJson(jsonString, new TypeToken<JsonObject>() {}.getType());
		// @formatter:on
	}

	public static Map<String, String> generateTokens(String username, String requestURL, String role) {
		Map<String, String> tokens = new HashMap<>();

		String accessToken = JWT.create()
		                        .withSubject(username)
		                        .withExpiresAt(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME))
		                        .withIssuer(requestURL) // URL of our application.
		                        .withClaim("role", role) // Only one role is in DB.
		                        .sign(ALGORITHM);

		String refreshToken = JWT.create()
		                         .withSubject(username)
		                         .withExpiresAt(new Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_EXPIRATION_TIME))
		                         .withIssuer(requestURL) // URL of our application.
		                         .sign(ALGORITHM);

		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);

		return tokens;
	}

	public static DecodedJWT checkAuthorizationToken(String fullToken) throws JWTVerificationException {
		if (!fullToken.startsWith(JWT_TOKEN_PREFIX)) {
			throw new JWTVerificationException(
					"Token does not start with the string '%s'".formatted(JWT_TOKEN_PREFIX)
			);
		}

		return JWT_VERIFIER.verify(fullToken.substring(JWT_TOKEN_PREFIX.length()));
	}

	public static String createServerMessage(String message, String type) {
		return JsonMessageWrapper.builder()
		                         /* Headers */
		                         .username("Server")
		                         .chatId("ALL")
		                         /*.sessionId(-1)*/
		                         .type(type)
		                         .date(System.currentTimeMillis())
		                         /*.token(null)*/
		                         /* Body */
		                         .encoding("UTF-8")
		                         .content(message)
		                         .build()
		                         /* JsonObject */
		                         .toString();
	}

	public static String createServerErrorMessage(String message) {
		return JsonMessageWrapper.builder()
		                         /* Headers */
		                         .username("Server")
		                         .chatId("NONE")
		                         /*.sessionId(-1)*/
		                         .type(ERROR_MESSAGE.type())
		                         .date(System.currentTimeMillis())
		                         /*.token(null)*/
		                         /* Body */
		                         .encoding("UTF-8")
		                         .content(message)
		                         .build()
		                         /* JsonObject */
		                         .toString();
	}

	/**
	 * Determines if the running environment is development or production.
	 *
	 * @return {@code true} if the environment is development, {@code false} otherwise.
	 */
	public static boolean isDevEnv() {
		return System.getenv("ENV").toLowerCase().startsWith("dev");
	}

	public static String jsonOf(String key, String value) {
		JsonObject json = new JsonObject();
		json.addProperty(key, value);
		return json.toString();
	}
}
