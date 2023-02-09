package rs.chat.net.ws.strategies.commands;

import rs.chat.exceptions.WebSocketException;

import java.io.IOException;

public interface CommandStrategy {
	/**
	 * Handles command with the instantiated strategy.
	 *
	 * @param handlingDTO {@link CommandHandlingDTO} containing the data needed for handling.
	 *
	 * @throws WebSocketException if error occurs during handling.
	 * @throws IOException        if error occurs during handling.
	 */
	void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException;
}
