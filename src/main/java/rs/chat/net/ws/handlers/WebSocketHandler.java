package rs.chat.net.ws.handlers;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClient;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.utils.Utils;

import java.io.IOException;

import static rs.chat.net.ws.WSMessage.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WSMessage.AUDIO_MESSAGE;
import static rs.chat.net.ws.WSMessage.ERROR_MESSAGE;
import static rs.chat.net.ws.WSMessage.IMAGE_MESSAGE;
import static rs.chat.net.ws.WSMessage.TEXT_MESSAGE;
import static rs.chat.net.ws.WSMessage.USER_CONNECTED;
import static rs.chat.net.ws.WSMessage.USER_DISCONNECTED;
import static rs.chat.net.ws.WSMessage.VIDEO_MESSAGE;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	private final WebSocketChatMap chatMap = new WebSocketChatMap();

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

		// FIXME: A user that did not send the USER_CONNECTED message could send messages
		//  but cannot receive them
		WSClientID wsClientID = new WSClientID(username, chatId, sessionId);
		WSMessage receivedMessageType = new WSMessage(type, null, null);

		if (USER_CONNECTED.equals(receivedMessageType)) {
			Utils.checkAuthorizationToken(token);

			this.chatMap.addClientToChat(new WSClient(session, wsClientID));
			this.chatMap.broadcastToSingleChatAndExcludeClient(
					wsClientID,
					Utils.createServerMessage(username + " has joined the chat", USER_CONNECTED.type())
			);
		} else if (USER_DISCONNECTED.equals(receivedMessageType)) {
			this.chatMap.removeClientFromChat(wsClientID);
			this.chatMap.broadcastToSingleChat(
					chatId,
					Utils.createServerMessage(username + " has disconnected from the chat", USER_DISCONNECTED.type())
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
					Utils.createServerMessage(
							"ERROR: type property is not present in the content of the JSON",
							ERROR_MESSAGE.type()
					))
			);
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.sendMessage(
				new TextMessage(Utils.createServerMessage(exception.getMessage(), ERROR_MESSAGE.type()))
		);
	}

	/**
	 * Removes the fields of the message received to be able to send it to
	 * other clients without sensitive information. In addition, it updates
	 * the {@code date} field. Only headers are modified.
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
