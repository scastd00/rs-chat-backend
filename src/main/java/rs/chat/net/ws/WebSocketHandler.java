package rs.chat.net.ws;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Base64;

import static rs.chat.net.ws.WSMessage.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WSMessage.AUDIO_MESSAGE;
import static rs.chat.net.ws.WSMessage.ERROR_MESSAGE;
import static rs.chat.net.ws.WSMessage.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.WSMessage.IMAGE_MESSAGE;
import static rs.chat.net.ws.WSMessage.TEXT_MESSAGE;
import static rs.chat.net.ws.WSMessage.USER_JOINED;
import static rs.chat.net.ws.WSMessage.USER_LEFT;
import static rs.chat.net.ws.WSMessage.VIDEO_MESSAGE;
import static rs.chat.utils.Utils.checkAuthorizationToken;
import static rs.chat.utils.Utils.createActiveUsersMessage;
import static rs.chat.utils.Utils.createServerErrorMessage;
import static rs.chat.utils.Utils.createServerMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private final WebSocketChatMap chatMap;

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
					createServerMessage(username + " has joined the chat", USER_JOINED.type(), chatId)
			);

			log.debug(username + " has joined the chat");
		} else if (USER_LEFT.equals(receivedMessageType)) {
			this.chatMap.broadcastToSingleChat(
					chatId,
					createServerMessage(username + " has disconnected from the chat", USER_LEFT.type(), chatId)
			);
			this.chatMap.removeClientFromChat(wsClientID);
			// Closed from the frontend

			log.debug(username + " has disconnected from the chat");
		} else if (TEXT_MESSAGE.equals(receivedMessageType)) {
			// Clear the sensitive data to send the message to other clients
			String response = this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage.getParsedPayload());
			this.chatMap.broadcastToSingleChatAndExcludeClient(wsClientID, response);
		} else if (IMAGE_MESSAGE.equals(receivedMessageType)) {
			log.info("");
		} else if (AUDIO_MESSAGE.equals(receivedMessageType)) {
			log.info("");
		} else if (VIDEO_MESSAGE.equals(receivedMessageType)) {
			log.info("");
		} else if (ACTIVE_USERS_MESSAGE.equals(receivedMessageType)) {
			log.info(username + " requested active users");

			session.sendMessage(
					new TextMessage(createActiveUsersMessage(this.chatMap.getUsernamesOfChat(chatId)))
			);
		} else if (GET_HISTORY_MESSAGE.equals(receivedMessageType)) {
			log.info(username + " requested history");
		} else {
			session.sendMessage(new TextMessage(
					createServerMessage(
							"ERROR: type property is not present in the content of the JSON",
							ERROR_MESSAGE.type(),
							chatId
					))
			);
		}
	}

	/**
	 * This method receives binary messages and treats them as needed.
	 * (Will be used for the media transferred through the websocket).
	 *
	 * @param session socket of the connected client.
	 * @param message binary message received from the client.
	 */
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		// Todo: send from client the next structure
		/*
		 * 1st byte: length of header data
		 * Next bytes: header containing metadata of the file received.
		 */
		try {
			session.sendMessage(new BinaryMessage(
					Base64.getEncoder().encode(message.getPayload())
			));
		} catch (IOException e) {
			try {
				session.sendMessage(new TextMessage(
						createServerMessage(
								"ERROR: type property is not present in the content of the JSON",
								ERROR_MESSAGE.type(),
								"TODO" // Todo: get the chat id
						))
				);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.sendMessage(
				new TextMessage(createServerErrorMessage(exception.getMessage()))
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
