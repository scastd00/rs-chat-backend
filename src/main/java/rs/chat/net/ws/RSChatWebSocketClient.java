package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;

import java.util.Objects;

@Slf4j
public class RSChatWebSocketClient {
	private final WebSocket socket;
	private final WSClientID wsClientID;

	public RSChatWebSocketClient(WebSocket socket, WSClientID wsClientID) {
		this.socket = socket;
		this.wsClientID = wsClientID;
	}

	public WSClientID getWSClientID() {
		return this.wsClientID;
	}

	public void send(String message) {
		this.socket.send(message);
	}

	public void close(int code, String message) {
		this.socket.close(code, message);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		RSChatWebSocketClient that = (RSChatWebSocketClient) o;

		return this.wsClientID.equals(that.wsClientID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.wsClientID);
	}
}
