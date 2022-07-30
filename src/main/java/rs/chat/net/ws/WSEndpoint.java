package rs.chat.net.ws;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import rs.chat.utils.Utils;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import static rs.chat.net.ws.WebSocketMessageType.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.AUDIO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.IMAGE_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.SERVER_INFO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.TEXT_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.USER_CONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.USER_DISCONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.VIDEO_MESSAGE;

@Slf4j
@ClientEndpoint
@ServerEndpoint(value = "/rschat/")
public class WSEndpoint {
	private final WSServer server = WSServer.getInstance();

	@OnOpen
	public void onConnect(Session session) {
		log.debug("Socket Connected: " + session);
		// Send to the client the time of the server, to sync all the messages
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		JsonObject jsonMessage = Utils.parseJson(message);

		JsonObject headers = (JsonObject) jsonMessage.get("headers");
		String username = headers.get("username").getAsString();
		String chatId = headers.get("chatId").getAsString();
		long sessionId = headers.get("sessionId").getAsLong();
		String type = headers.get("type").getAsString();
//		long date = headers.get("date").getAsLong();
//		String token = headers.get("token").getAsString();

//		JsonObject body = (JsonObject) jsonMessage.get("body");
//		String encoding = body.get("encoding").getAsString();
//		String content = body.get("content").getAsString();
		WSClientID wsClientID = new WSClientID(username, chatId, sessionId);

		switch (type) {
			case USER_CONNECTED -> {
				this.server.getChatMap().addClientToChat(new WSClient(session, wsClientID));
				this.server.getChatMap().broadcastToSingleChatAndExcludeClient(
						wsClientID,
						Utils.createServerMessage(username + " has joined the chat", USER_CONNECTED)
				);
			}

			case USER_DISCONNECTED -> {
				this.server.getChatMap().removeClientFromChat(wsClientID);
				this.server.getChatMap()
				           .broadcastToSingleChat(
						           chatId,
						           Utils.createServerMessage(username + " has disconnected from the chat", USER_DISCONNECTED)
				           );
				// Closed from the frontend
			}

			case TEXT_MESSAGE -> {
				// Clear the sensitive data to send the message to other clients
				String response = this.clearSensitiveDataChangeDateAndBuildResponse(jsonMessage);
				this.server.getChatMap()
				           .broadcastToSingleChatAndExcludeClient(wsClientID, response);
			}

			case IMAGE_MESSAGE -> log.info("");

			case AUDIO_MESSAGE -> log.info("");

			case VIDEO_MESSAGE -> log.info("");

			case ACTIVE_USERS_MESSAGE -> log.info("");

			default -> session.getBasicRemote()
			                  .sendText(Utils.createServerMessage("ERROR: type property is not present in the content of the JSON",
			                                                      SERVER_INFO_MESSAGE));
		}

		log.info("Message: " + message);
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		log.debug("Socket Disconnected: " + session);
	}

	@OnError
	public void onError(Throwable cause) {
		cause.printStackTrace();
	}

	/**
	 * Removes the fields of the message received to be able to send it to
	 * other clients without sensitive information. In addition, it appends
	 * a new {@code date} field. Only headers are modified.
	 *
	 * @param message received message to remove fields.
	 *
	 * @return a new {@link String} message without the sensitive information
	 * and a new field.
	 */
	private String clearSensitiveDataChangeDateAndBuildResponse(JsonObject message) {
		JsonObject headers = (JsonObject) message.get("headers");
		headers.remove("sessionId");
		headers.remove("token");
		headers.addProperty("date", System.currentTimeMillis()); // Modify property
		return message.toString();
	}
}
