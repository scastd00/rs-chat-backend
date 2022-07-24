package rs.chat.net.ws;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import rs.chat.utils.Utils;

import java.net.InetSocketAddress;

import static rs.chat.net.ws.WebSocketMessageType.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.AUDIO_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.END_CONNECTION;
import static rs.chat.net.ws.WebSocketMessageType.IMAGE_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.KEEP_ALIVE_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.NEW_USER;
import static rs.chat.net.ws.WebSocketMessageType.TEXT_MESSAGE;
import static rs.chat.net.ws.WebSocketMessageType.USER_JOINED;
import static rs.chat.net.ws.WebSocketMessageType.VIDEO_MESSAGE;

@Slf4j
public class RSChatWebSocketServer extends WebSocketServer {
	private static final RSChatWebSocketServer INSTANCE = new RSChatWebSocketServer();
	private static final int KB_64 = 65536;

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
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome to the server!");

		InetSocketAddress remoteSocketAddress = conn.getRemoteSocketAddress();
		String hostAddress = remoteSocketAddress.getAddress().getHostAddress();
		int port = remoteSocketAddress.getPort();

		log.info(hostAddress + ":" + port + " entered the room!");

		// Broadcast in the chat that a client has connected.
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		// Broadcast in the chat that a client has disconnected.
		log.info(conn + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		JsonObject jsonObject = Utils.parseJson(message);

		JsonObject headers = (JsonObject) jsonObject.get("headers");
		String username = headers.get("username").getAsString();
		String chatId = headers.get("chatId").getAsString();
		JsonElement dateSignIn = headers.get("dateSignIn"); // Could be null, so we take the value when we need it

		JsonObject body = (JsonObject) jsonObject.get("body");
		String encoding = body.get("encoding").getAsString();
		String content = body.get("content").getAsString();

		switch (jsonObject.get("type").getAsString()) {
			case NEW_USER -> this.chatMap.addClientToChat(new RSChatWebSocketClient(conn, username, chatId));
			case END_CONNECTION -> this.chatMap.getClientByUsernameAndDate(chatId, username, dateSignIn.getAsLong());
			case USER_JOINED -> log.info("");
			case TEXT_MESSAGE -> log.info("");
			case IMAGE_MESSAGE -> log.info("");
			case AUDIO_MESSAGE -> log.info("");
			case VIDEO_MESSAGE -> log.info("");
			case KEEP_ALIVE_MESSAGE -> log.info("");
			case ACTIVE_USERS_MESSAGE -> log.info("");

			default -> conn.send(Utils.shortJsonString("error", "type property is not present in the content of the JSON"));
		}

		log.info("Message: " + message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {

	}

	@Override
	public void onStart() {
		log.info("Server started at port " + this.getPort());
		this.setConnectionLostTimeout(0);
		this.setReuseAddr(true);
	}
}
