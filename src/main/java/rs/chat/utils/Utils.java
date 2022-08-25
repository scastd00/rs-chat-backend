package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

/**
 * Utility class for common operations.
 */
public final class Utils {
	private Utils() {
	}

	/**
	 * Parses a JSON string into a {@link JsonObject}.
	 *
	 * @param jsonString the JSON string to parse into a {@link JsonObject}.
	 *
	 * @return the {@link JsonObject} parsed from the JSON string.
	 */
	public static JsonObject parseJson(String jsonString) {
		return GSON.fromJson(jsonString, JsonObject.class);
	}

	/**
	 * Creates the tokens that are used to authenticate the user.
	 * 2 tokens are created:
	 * <ul>
	 *     <li>Access token: this token is used to authenticate the user.</li>
	 *     <li>Refresh token: this token is used to refresh the access token.</li>
	 * </ul>
	 *
	 * @param username   the username of the user.
	 * @param requestURL the URL of the request.
	 * @param role       the role of the user.
	 *
	 * @return the tokens that are used to authenticate the user.
	 */
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

	/**
	 * Verifies the access token.
	 *
	 * @param fullToken the full token to verify.
	 *
	 * @return the decoded JWT token.
	 *
	 * @throws JWTVerificationException if the token is invalid.
	 */
	public static DecodedJWT checkAuthorizationToken(String fullToken) throws JWTVerificationException {
		if (!fullToken.startsWith(JWT_TOKEN_PREFIX)) {
			throw new JWTVerificationException(
					"Token does not start with the string '%s'".formatted(JWT_TOKEN_PREFIX)
			);
		}

		return JWT_VERIFIER.verify(fullToken.substring(JWT_TOKEN_PREFIX.length()));
	}

	/**
	 * Creates a {@link String} message containing the active users given a
	 * {@link List<String>} of usernames.
	 *
	 * @param usernames the {@link List<String>} of usernames.
	 *
	 * @return the {@link String} message containing the active users.
	 */
	public static String createActiveUsersMessage(List<String> usernames) {
		JsonArray usersArray = new JsonArray();
		usernames.forEach(usersArray::add);
		// Todo: get the chatId to send the message to (in this case is a user).
		return createServerMessage(usersArray.toString(), ACTIVE_USERS_MESSAGE.type(), "TODO");
	}

	/**
	 * Creates a {@link String} message containing a server message.
	 *
	 * @param message the message to send.
	 * @param type    the type of the message.
	 * @param chatId  the chatId to send the message to.
	 *
	 * @return the {@link String} message containing the server message.
	 */
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

	/**
	 * Creates a {@link String} message containing an error message.
	 *
	 * @param message the error message to send.
	 *
	 * @return the {@link String} message containing the error message.
	 */
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
	 * Determines if the running environment is DEVELOPMENT or PRODUCTION.
	 *
	 * @return {@code true} if the environment is DEVELOPMENT, {@code false} otherwise.
	 */
	public static boolean isDevEnv() {
		return System.getenv("ENV").toLowerCase().startsWith("dev");
	}

	/**
	 * Adds a {@link Number} to a {@link JsonObject} and returns the {@link String} representation.
	 *
	 * @param key   the key of the {@link Number} to add.
	 * @param value the value of the {@link Number} to add.
	 *
	 * @return the {@link String} representation of the {@link JsonObject}.
	 */
	public static String jsonOfNumber(String key, Number value) {
		JsonObject json = new JsonObject();
		json.addProperty(key, value);
		return json.toString();
	}
}
