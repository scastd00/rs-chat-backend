package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;

/**
 * Record to keep track of a client's connection.
 *
 * @param session  {@link WebSocketSession} of the client.
 * @param clientID {@link ClientID} of the client.
 */
@Slf4j
public record Client(WebSocketSession session, ClientID clientID) {
	/**
	 * Send a message to the client.
	 *
	 * @param message message to send.
	 */
	public synchronized void send(String message) {
		try {
			this.session.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			log.error("Could not send message to client", e);
		}
	}

	/**
	 * Close the client's connection.
	 */
	public void close() {
		try {
			if (this.session.isOpen()) {
				this.session.close();
			}
		} catch (IOException e) {
			log.error("Could not close socket normally", e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		Client that = (Client) o;

		return this.clientID.equals(that.clientID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.clientID);
	}
}
