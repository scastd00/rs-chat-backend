package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;

import java.util.Objects;

@Slf4j
public class RSChatWebSocketClient {
	private final WebSocket socket;
	private final String username;
	private final String chatId;
	private final long sessionId;

	public RSChatWebSocketClient(WebSocket socket, String username, String chatId, long sessionId) {
		this.socket = socket;
		this.username = username;
		this.chatId = chatId;
		this.sessionId = sessionId;
	}

	public String getUsername() {
		return this.username;
	}

	public String getChatId() {
		return this.chatId;
	}

	public long getSessionId() {
		return this.sessionId;
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

		return this.getUsername().equals(that.getUsername()) &&
				this.getChatId().equals(that.getChatId()) &&
				this.getSessionId() == that.getSessionId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getUsername(), this.getChatId(), this.getSessionId());
	}
}
