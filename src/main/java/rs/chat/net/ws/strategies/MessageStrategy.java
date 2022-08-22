package rs.chat.net.ws.strategies;

import com.auth0.jwt.exceptions.JWTVerificationException;
import rs.chat.exceptions.TokenValidationException;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

import static rs.chat.utils.Utils.checkAuthorizationToken;

public interface MessageStrategy {
	void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	            Map<String, Object> otherData) throws WebSocketException, IOException;

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
}
