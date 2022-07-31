package rs.chat.net.ws.handlers;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.net.ws.WSClient;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.utils.Utils;

import java.io.IOException;

import static rs.chat.net.ws.WebSocketMessageType.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.AUDIO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.ERROR_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.IMAGE_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.TEXT_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.USER_CONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.USER_DISCONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.VIDEO_MESSAGE;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	private final WebSocketChatMap chatMap = new WebSocketChatMap();

	@Override
	protected void handleTextMessage(@NotNull WebSocketSession session,
	                                 @NotNull TextMessage message) throws IOException {
		JsonObject jsonMessage = Utils.parseJson(message.getPayload());

		JsonObject headers = (JsonObject) jsonMessage.get("headers");
		String username = headers.get("username").getAsString();
		String chatId = headers.get("chatId").getAsString();
		long sessionId = headers.get("sessionId").getAsLong();
		String type = headers.get("type").getAsString();
//		long date = headers.get("date").getAsLong();
		String token = headers.get("token").getAsString();

//		JsonObject body = (JsonObject) jsonMessage.get("body");
//		String encoding = body.get("encoding").getAsString();
//		String content = body.get("content").getAsString();

		// FIXME: A user that did not send the USER_CONNECTED message could send messages
		//  but cannot receive them
		WSClientID wsClientID = new WSClientID(username, chatId, sessionId);

		switch (type) {
			case USER_CONNECTED -> {
				Utils.checkAuthorizationToken(token);

				this.chatMap.addClientToChat(new WSClient(session, wsClientID));
				this.chatMap.broadcastToSingleChatAndExcludeClient(
						wsClientID,
						Utils.createServerMessage(username + " has joined the chat", USER_CONNECTED)
				);
			}

			case USER_DISCONNECTED -> {
				this.chatMap.removeClientFromChat(wsClientID);
				this.chatMap.broadcastToSingleChat(
						chatId,
						Utils.createServerMessage(username + " has disconnected from the chat", USER_DISCONNECTED)
				);
				// Closed from the frontend
			}

			case TEXT_MESSAGE -> {
				// Clear the sensitive data to send the message to other clients
				String response = this.clearSensitiveDataChangeDateAndBuildResponse(jsonMessage);
				this.chatMap.broadcastToSingleChatAndExcludeClient(wsClientID, response);
			}

			case IMAGE_MESSAGE -> log.info("");

			case AUDIO_MESSAGE -> log.info("");

			case VIDEO_MESSAGE -> log.info("");

			case ACTIVE_USERS_MESSAGE -> log.info("");

			default -> session.sendMessage(
					new TextMessage(
							Utils.createServerMessage(
									"ERROR: type property is not present in the content of the JSON",
									ERROR_MESSAGE
							)
					)
			);
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.sendMessage(
				new TextMessage(Utils.createServerMessage(exception.getMessage(), ERROR_MESSAGE))
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
