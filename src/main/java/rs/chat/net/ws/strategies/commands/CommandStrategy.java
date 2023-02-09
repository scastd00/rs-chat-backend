package rs.chat.net.ws.strategies.commands;

import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ClientID;

import java.io.IOException;
import java.util.Map;

public interface CommandStrategy {
	/**
	 * Handles command with the instantiated strategy.
	 *
	 * @param handlingDTO@throws WebSocketException if error occurs during handling.
	 *
	 * @throws IOException if error occurs during handling.
	 */
	void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException;

	/**
	 * Method to ease the process of sending a message to a specific {@link WebSocketSession}.
	 *
	 * @param otherData {@link Map} containing the session to send the message to.
	 *
	 * @return {@link WebSocketSession} to send the message to.
	 */
	default WebSocketSession getSession(Map<String, Object> otherData) {
		return (WebSocketSession) otherData.get("session");
	}

	/**
	 * Method to ease the process of getting the {@link ClientID} of a specific {@link WebSocketSession}.
	 *
	 * @param otherData {@link Map} containing the session to get the {@link ClientID} from.
	 *
	 * @return {@link ClientID} of the session.
	 */
	default ClientID getClientID(Map<String, Object> otherData) {
		return (ClientID) otherData.get("clientID");
	}
}
