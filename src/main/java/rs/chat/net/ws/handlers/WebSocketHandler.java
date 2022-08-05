package rs.chat.net.ws.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClient;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;

import static rs.chat.net.ws.WSMessage.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WSMessage.AUDIO_MESSAGE;
import static rs.chat.net.ws.WSMessage.ERROR_MESSAGE;
import static rs.chat.net.ws.WSMessage.IMAGE_MESSAGE;
import static rs.chat.net.ws.WSMessage.TEXT_MESSAGE;
import static rs.chat.net.ws.WSMessage.USER_JOINED;
import static rs.chat.net.ws.WSMessage.USER_LEFT;
import static rs.chat.net.ws.WSMessage.VIDEO_MESSAGE;
import static rs.chat.utils.Utils.checkAuthorizationToken;
import static rs.chat.utils.Utils.createServerErrorMessage;
import static rs.chat.utils.Utils.createServerMessage;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	private final WebSocketChatMap chatMap = new WebSocketChatMap();

	/**
	 * Handles text messages (JSON string).
	 *
	 * @param session remote session of the client in the server.
	 * @param message message received from the client.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	protected void handleTextMessage(@NotNull WebSocketSession session,
	                                 @NotNull TextMessage message) throws IOException {
		JsonMessageWrapper wrappedMessage = new JsonMessageWrapper(message.getPayload());

		String username = wrappedMessage.username();
		String chatId = wrappedMessage.chatId();
		long sessionId = wrappedMessage.sessionId();
		String type = wrappedMessage.type();
//		long date = wrappedMessage.date();
		String token = wrappedMessage.token();

//		String encoding = wrappedMessage.encoding();
//		String content = wrappedMessage.content();

		// FIXME: A user that did not send the USER_JOINED message could send messages
		//  but cannot receive them
		WSClientID wsClientID = new WSClientID(username, chatId, sessionId);
		WSMessage receivedMessageType = new WSMessage(type, null, null);

		if (USER_JOINED.equals(receivedMessageType)) {
			try {
				checkAuthorizationToken(token);
			} catch (JWTVerificationException e) {
				log.error("Error in token: {}", e.getMessage());

				session.sendMessage(new TextMessage(
						createServerErrorMessage("Token is malformed or expired, please log in again")
				));

				return;
			}

			this.chatMap.addClientToChat(new WSClient(session, wsClientID));
			this.chatMap.broadcastToSingleChatAndExcludeClient(
					wsClientID,
					createServerMessage(username + " has joined the chat", USER_JOINED.type())
			);
		} else if (USER_LEFT.equals(receivedMessageType)) {
			this.chatMap.removeClientFromChat(wsClientID);
			this.chatMap.broadcastToSingleChat(
					chatId,
					createServerMessage(username + " has disconnected from the chat", USER_LEFT.type())
			);
			// Closed from the frontend
		} else if (TEXT_MESSAGE.equals(receivedMessageType)) {// Clear the sensitive data to send the message to other clients
			String response = this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage.getParsedPayload());
			this.chatMap.broadcastToSingleChatAndExcludeClient(wsClientID, response);
		} else if (IMAGE_MESSAGE.equals(receivedMessageType)) {
			log.info("");
		} else if (AUDIO_MESSAGE.equals(receivedMessageType)) {
			log.info("");
		} else if (VIDEO_MESSAGE.equals(receivedMessageType)) {
			log.info("");
		} else if (ACTIVE_USERS_MESSAGE.equals(receivedMessageType)) {
			log.info("");
		} else {
			session.sendMessage(new TextMessage(
					createServerMessage(
							"ERROR: type property is not present in the content of the JSON",
							ERROR_MESSAGE.type()
					))
			);
		}
	}

	/**
	 * This method receives binary messages and treats them as needed.
	 * (Will be used for the media transferred through the websocket).
	 *
	 * @param session
	 * @param message
	 */
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		super.handleBinaryMessage(session, message);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.sendMessage(
				new TextMessage(createServerMessage(exception.getMessage(), ERROR_MESSAGE.type()))
		);
	}

	/**
	 * Removes the fields of the message received to be able to send it to
	 * other clients without sensitive information. In addition, it updates
	 * the {@code date} field. NOTE: Only headers are modified.
	 *
	 * @param message received message to remove fields.
	 *
	 * @return the {@link String} message without the sensitive information
	 * and the date of the server.
	 */
	private String clearSensitiveDataChangeDateAndBuildResponse(JsonObject message) {
		JsonObject headers = (JsonObject) message.get("headers");
		headers.remove("sessionId");
		headers.remove("token");
		headers.addProperty("date", System.currentTimeMillis()); // Modify property
		return message.toString();
	}
}
