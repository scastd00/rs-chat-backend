package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.server.ServerEndpoint;
import java.util.Objects;

@ClientEndpoint
@ServerEndpoint("/ws/chat/")
@Slf4j
public class RSChatWebSocketClient {
	private final WebSocket socket;
	private final String username;
	private final String chatId;
	private final long created = System.currentTimeMillis();

	public RSChatWebSocketClient(WebSocket socket, String username, String chatId) {
		this.socket = socket;
		this.username = username;
		this.chatId = chatId;
	}

	public String getUsername() {
		return this.username;
	}

	public String getChatId() {
		return this.chatId;
	}

	public long getCreated() {
		return this.created;
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
				this.getCreated() == that.getCreated();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getUsername(), this.getChatId(), this.getCreated());
	}
}
