package rs.chat.net.ws.strategies.commands;

import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;

import java.io.IOException;
import java.util.Map;

public interface CommandStrategy {
	/**
	 * Handles command with the instantiated strategy.
	 *
	 * @param chatManagement map containing all available {@link rs.chat.net.ws.Chat}s.
	 * @param otherData      additional data: <ul>
	 *                       <li>Username related to the command.</li>
	 *                       <li>Some message to send with the command.</li>
	 *                       </ul>
	 *
	 * @throws WebSocketException if error occurs during handling.
	 * @throws IOException        if error occurs during handling.
	 */
	void handle(ChatManagement chatManagement, Map<String, Object> otherData)
			throws WebSocketException, IOException;

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
