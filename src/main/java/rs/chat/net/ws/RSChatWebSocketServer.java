package rs.chat.net.ws;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import rs.chat.utils.Utils;

import java.net.InetSocketAddress;

import static rs.chat.net.ws.WebSocketMessageType.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.AUDIO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.IMAGE_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.TEXT_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.USER_CONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.USER_DISCONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.VIDEO_MESSAGE;

@Slf4j
public class RSChatWebSocketServer extends WebSocketServer {
	private static final RSChatWebSocketServer INSTANCE = new RSChatWebSocketServer();

	private final WebSocketChatMap chatMap = new WebSocketChatMap();

	public RSChatWebSocketServer() {
		this(9090);
	}

	public RSChatWebSocketServer(int port) {
		this(new InetSocketAddress(port));
	}

	public RSChatWebSocketServer(InetSocketAddress address) {
		super(address);
	}

	public static RSChatWebSocketServer getInstance() {
		return INSTANCE;
	}

	@Override
	public void onOpen(WebSocket socket, ClientHandshake handshake) {
		socket.send("Welcome to the server!");

		InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
		String hostAddress = remoteSocketAddress.getAddress().getHostAddress();
		int port = remoteSocketAddress.getPort();

		log.info(hostAddress + ":" + port + " entered the room!");

		// Broadcast in the chat that a client has connected.
	}

	@Override
	public void onClose(WebSocket socket, int code, String reason, boolean remote) {
		// Broadcast in the chat that a client has disconnected.
		log.info(socket + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket socket, String message) {
		JsonObject jsonMessage = Utils.parseJson(message);

		JsonObject headers = (JsonObject) jsonMessage.get("headers");
		String username = headers.get("username").getAsString();
		String chatId = headers.get("chatId").getAsString();
		long sessionId = headers.get("sessionId").getAsLong();
		String type = headers.get("type").getAsString();
//		String token = headers.get("token").getAsString();

//		JsonObject body = (JsonObject) jsonMessage.get("body");
//		String encoding = body.get("encoding").getAsString();
//		String content = body.get("content").getAsString();

		switch (type) {
			case USER_CONNECTED -> {
				RSChatWebSocketClient client = new RSChatWebSocketClient(socket, username, chatId, sessionId);
				this.chatMap.addClientToChat(client);
				this.chatMap.broadcastToSingleChatAndExcludeClient(chatId, Utils.createServerMessage(username + " has joined the chat"), client);
			}

			case USER_DISCONNECTED -> {
				RSChatWebSocketClient remoteClient = this.getRsChatWebSocketClient(username, chatId, sessionId);
				this.chatMap.removeClientFromChat(remoteClient);
				this.chatMap.broadcastToSingleChat(chatId, Utils.createServerMessage(username + " has disconnected from the chat"));
				// Closed from the frontend
			}

			case TEXT_MESSAGE -> {
				// Clear the sensitive data to send the message to other clients
				String response = this.clearSensitiveDataAndBuildResponse(jsonMessage);
				RSChatWebSocketClient remoteClient = this.getRsChatWebSocketClient(username, chatId, sessionId);
				this.chatMap.broadcastToSingleChatAndExcludeClient(chatId, response, remoteClient);
			}

			case IMAGE_MESSAGE -> log.info("");

			case AUDIO_MESSAGE -> log.info("");

			case VIDEO_MESSAGE -> log.info("");

			case ACTIVE_USERS_MESSAGE -> log.info("");

			default -> socket.send(Utils.shortJsonString("error", "type property is not present in the content of the JSON"));
		}

		log.info("Message: " + message);
	}

	private String clearSensitiveDataAndBuildResponse(JsonObject message) {
		JsonObject headers = (JsonObject) message.get("headers");
		headers.remove("sessionId");
		headers.remove("token");
		headers.addProperty("date", System.currentTimeMillis());
		// body remains unmodified
		return message.toString();
	}

	private RSChatWebSocketClient getRsChatWebSocketClient(String username, String chatId, long sessionId) {
		return this.chatMap.getClientByUsernameAndDate(chatId,
		                                               username,
		                                               sessionId);
	}

	@Override
	public void onError(WebSocket socket, Exception ex) {

	}

	@Override
	public void onStart() {
		log.info("Server started at port " + this.getPort());
		this.setConnectionLostTimeout(0);
		this.setReuseAddr(true);
	}
}
