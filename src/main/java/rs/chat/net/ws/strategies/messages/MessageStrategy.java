package rs.chat.net.ws.strategies.messages;

import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
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
