package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RSChatWSServer extends WebSocketServer {
	private static final RSChatWSServer INSTANCE = new RSChatWSServer(9090);
	private final CopyOnWriteArrayList<RSChatWebSocket> clients = new CopyOnWriteArrayList<>();
	private static final int KB_64 = 65536;

	public RSChatWSServer(int port) {
		super(new InetSocketAddress(port));
	}

	public RSChatWSServer(InetSocketAddress address) {
		super(address);
	}

	public static RSChatWSServer getInstance() {
		return INSTANCE;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome to the server!"); // This method sends a message to the new client
		this.broadcast("new connection: " + handshake.getResourceDescriptor()); // This method sends a message to all clients connected
		log.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		broadcast(conn + " has left the room!");
		System.out.println(conn + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		broadcast(message);
		System.out.println(conn + ": " + message);
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		broadcast(message.array());
		System.out.println(conn + ": " + message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {

	}

	@Override
	public void onStart() {
		log.info("Server started at port " + this.getPort());
		this.setConnectionLostTimeout(0);
	}
}
