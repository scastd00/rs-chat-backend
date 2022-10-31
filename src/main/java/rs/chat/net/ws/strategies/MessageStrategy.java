package rs.chat.net.ws.strategies;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

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
}
