package rs.chat.net.ws.strategies.messages;

import org.springframework.web.socket.WebSocketSession;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;

import java.util.Map;

public record MessageHandlingDTO(JsonMessageWrapper wrappedMessage, Map<String, Object> otherData) {
	/**
	 * Method to ease the process of sending a message to a specific {@link WebSocketSession}.
	 *
	 * @return {@link WebSocketSession} to send the message to.
	 */
	public WebSocketSession getSession() {
		return (WebSocketSession) otherData.get("session");
	}

	/**
	 * Method to ease the process of getting the {@link ClientID} of a specific {@link WebSocketSession}.
	 *
	 * @return {@link ClientID} of the session.
	 */
	public ClientID getClientID() {
		return (ClientID) otherData.get("clientID");
	}
}
