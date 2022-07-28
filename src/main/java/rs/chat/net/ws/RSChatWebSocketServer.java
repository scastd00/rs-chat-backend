package rs.chat.net.ws;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import rs.chat.utils.Constants;
import rs.chat.utils.Utils;

import java.net.InetSocketAddress;

import static rs.chat.net.ws.WebSocketMessageType.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.AUDIO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.IMAGE_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.SERVER_INFO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.TEXT_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.USER_CONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.USER_DISCONNECTED;
import static rs.chat.net.ws.WebSocketMessageType.VIDEO_MESSAGE;

@Slf4j
public class RSChatWebSocketServer extends WebSocketServer {
	private static final RSChatWebSocketServer INSTANCE = new RSChatWebSocketServer();
	private final WebSocketChatMap chatMap = new WebSocketChatMap();

	/**
	 * Default constructor. Creates an instance with the port specified in
	 * the application.properties file.
	 */
	public RSChatWebSocketServer() {
		this(Constants.getWSPort());
	}

	/**
	 * Creates an instance with the specified port.
	 *
	 * @param port port to which the server will be bound.
	 */
	public RSChatWebSocketServer(int port) {
		super(new InetSocketAddress(port));
	}

	/**
	 * Returns the single instance of the class.
	 *
	 * @return instance of the class.
	 */
	public static RSChatWebSocketServer getInstance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onOpen(WebSocket socket, ClientHandshake handshake) {
//		socket.send("Welcome to the server!");

		InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
		String hostAddress = remoteSocketAddress.getAddress().getHostAddress();
		int port = remoteSocketAddress.getPort();

		log.info(hostAddress + ":" + port + " entered the room!");

		// Broadcast in the chat that a client has connected.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClose(WebSocket socket, int code, String reason, boolean remote) {
		// Broadcast in the chat that a client has disconnected.
		log.info(socket + " has left the room!");
	}

	/**
	 * {@inheritDoc}
	 */
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
		WSClientID wsClientID = new WSClientID(username, chatId, sessionId);

		switch (type) {
			case USER_CONNECTED -> {
				this.chatMap.addClientToChat(
						new RSChatWebSocketClient(socket, wsClientID)
				);
				this.chatMap.broadcastToSingleChatAndExcludeClient(
						wsClientID, Utils.createServerMessage(username + " has joined the chat", USER_CONNECTED)
				);
			}

			case USER_DISCONNECTED -> {
				this.chatMap.removeClientFromChat(wsClientID);
				this.chatMap.broadcastToSingleChat(chatId, Utils.createServerMessage(username + " has disconnected from the chat",
				                                                                     USER_DISCONNECTED));
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

			default -> socket.send(Utils.createServerMessage("ERROR: type property is not present in the content of the JSON", SERVER_INFO_MESSAGE));
		}

		log.info("Message: " + message);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onError(WebSocket socket, Exception ex) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStart() {
		log.info("Server started at port " + this.getPort());
		this.setConnectionLostTimeout(0);
		this.setReuseAddr(true);
	}
}
