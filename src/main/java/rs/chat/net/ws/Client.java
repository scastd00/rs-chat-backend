package rs.chat.net.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Client {
	private final WebSocketSession session;
	private final ClientID clientID;
	private boolean away = false;

	/**
	 * Send a message to the client. Since the session is thread-safe (see
	 * {@link org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator ConcurrentWebSocketSessionDecorator}),
	 * messages are "enqueued" and we don't need to synchronize this method.
	 *
	 * @param message message to send.
	 */
	public void send(String message) {
		try {
			this.session.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			log.error("Could not send message to client", e);
		}
	}

	public boolean canSend() {
		return this.session != null && this.session.isOpen();
	}

	public boolean isActive() {
		return !this.away;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.clientID);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		Client that = (Client) o;

		return this.clientID.equals(that.clientID);
	}
}
