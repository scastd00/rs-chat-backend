package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import rs.chat.net.ws.JsonMessageWrapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WSMessage.ERROR_MESSAGE;
import static rs.chat.utils.Constants.ALGORITHM;
import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static rs.chat.utils.Constants.JWT_VERIFIER;
import static rs.chat.utils.Constants.REFRESH_TOKEN_EXPIRATION_DURATION;
import static rs.chat.utils.Constants.TOKEN_EXPIRATION_DURATION;

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
		                        .withExpiresAt(Instant.now().plus(TOKEN_EXPIRATION_DURATION)) // 4 hours
		                        .withIssuer(requestURL) // URL of our application.
		                        .withClaim("role", role) // Only one role is in DB.
		                        .sign(ALGORITHM);

		String refreshToken = JWT.create()
		                         .withSubject(username)
		                         .withExpiresAt(Instant.now().plus(REFRESH_TOKEN_EXPIRATION_DURATION))
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

	public static String createActiveUsersMessage(List<String> usernames) {
		JsonArray usersArray = new JsonArray();
		usernames.forEach(usersArray::add);
		// Todo: get the chatId to send the message to (in this case is a user).
		return createServerMessage(usersArray.toString(), ACTIVE_USERS_MESSAGE.type(), "TODO");
	}

	public static String createServerMessage(String message, String type, String chatId) {
		return JsonMessageWrapper.builder()
		                         /* Headers */
		                         .username("Server")
		                         .chatId(chatId)
		                         .type(type)
		                         .date(System.currentTimeMillis())
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
		                         .type(ERROR_MESSAGE.type())
		                         .date(System.currentTimeMillis())
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

	public static String jsonOfNumber(String key, Number value) {
		JsonObject json = new JsonObject();
		json.addProperty(key, value);
		return json.toString();
	}
}
