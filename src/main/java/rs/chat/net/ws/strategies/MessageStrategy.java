package rs.chat.net.ws.strategies;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.JsonObject;
import rs.chat.exceptions.TokenValidationException;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

import static rs.chat.utils.Utils.checkAuthorizationToken;

/**
 * Strategy for handling messages.
 */
public interface MessageStrategy {
	/**
	 * Handles message with the instantiated strategy.
	 *
	 * @param wrappedMessage   message to handle in JSON format.
	 * @param webSocketChatMap map containing all available {@link rs.chat.net.ws.Chat}s.
	 * @param otherData        additional data.
	 *
	 * @throws WebSocketException if error occurs during handling.
	 * @throws IOException        if error occurs during handling.
	 */
	void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	            Map<String, Object> otherData) throws WebSocketException, IOException;

	/**
	 * Checks if the token is valid before handling the message.
	 *
	 * @param token token to check.
	 *
	 * @throws WebSocketException       if token is null or empty.
	 * @throws TokenValidationException if token is invalid.
	 */
	default void checkTokenValidity(String token) throws WebSocketException, TokenValidationException {
		if (token == null) {
			throw new WebSocketException("Token is null");
		}

		if (token.isEmpty()) {
			throw new WebSocketException("Token is empty");
		}

		try {
			checkAuthorizationToken(token);
		} catch (JWTVerificationException e) {
			throw new TokenValidationException(e.getMessage());
		}
	}

	/**
	 * Removes the fields of the message received to be able to send it to
	 * other clients without sensitive information. In addition, it updates
	 * the {@code date} field. NOTE: Only headers are modified.
	 *
	 * @param message received message to remove sensitive fields.
	 *
	 * @return the {@link String} message without the sensitive information
	 * and the actual date of the server.
	 */
	default String clearSensitiveDataChangeDateAndBuildResponse(JsonObject message) {
		JsonObject headers = (JsonObject) message.get("headers");
		headers.remove("sessionId");
		headers.remove("token");
		headers.addProperty("date", System.currentTimeMillis()); // Modify property
		return message.toString();
	}
}
