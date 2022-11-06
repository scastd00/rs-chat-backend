package rs.chat.net.ws.strategies.messages;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling messages.
 */
public interface MessageStrategy {
	/**
	 * Handles message with the instantiated strategy.
	 *
	 * @param wrappedMessage message to handle in JSON format.
	 * @param chatManagement map containing all available {@link rs.chat.net.ws.Chat}s.
	 * @param otherData      additional data.
	 *
	 * @throws WebSocketException if error occurs during handling.
	 * @throws IOException        if error occurs during handling.
	 */
	void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	            Map<String, Object> otherData) throws WebSocketException, IOException;
}