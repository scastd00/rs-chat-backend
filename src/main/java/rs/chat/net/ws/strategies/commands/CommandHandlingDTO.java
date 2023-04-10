package rs.chat.net.ws.strategies.commands;

import org.springframework.web.socket.WebSocketSession;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.strategies.commands.parser.Params;

import java.util.Map;

/**
 * Record that holds the data needed for the {@link CommandStrategy} to work.
 *
 * @param chatManagement {@link ChatManagement} instance.
 * @param otherData      Map of other data needed for the {@link CommandStrategy} to work.
 */
public record CommandHandlingDTO(ChatManagement chatManagement, Map<String, Object> otherData) {
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

	/**
	 * Method to ease the process of getting the {@link Params} of a specific message
	 * received from a {@link WebSocketSession}.
	 *
	 * @return {@link Params} of the session.
	 */
	public Params getParams() {
		return (Params) otherData.get(Params.class.getSimpleName());
	}
}
