package rs.chat.net.ws.strategies.messages;

import rs.chat.exceptions.WebSocketException;

import java.io.IOException;

/**
 * Strategy for handling messages.
 */
public interface MessageStrategy {
	/**
	 * Handles message with the instantiated strategy.
	 *
	 * @param handlingDTO DTO containing data for handling all the needed objects.
	 *
	 * @throws WebSocketException if error occurs during handling.
	 * @throws IOException        if error occurs during handling.
	 */
	void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException;
}
