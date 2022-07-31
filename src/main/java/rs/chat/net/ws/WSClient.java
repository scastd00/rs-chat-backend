package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class WSClient {
	private final WebSocketSession session;
	private final WSClientID wsClientID;

	public WSClient(WebSocketSession session, WSClientID wsClientID) {
		this.session = session;
		this.wsClientID = wsClientID;
	}

	public WSClientID getWSClientID() {
		return this.wsClientID;
	}

	public synchronized void send(String message) {
		try {
			this.session.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			log.error("Could not send message to client", e);
		}
	}

	public void close() {
		try {
			this.session.close();
		} catch (IOException e) {
			log.error("Could not close socket normally", e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		WSClient that = (WSClient) o;

		return this.wsClientID.equals(that.wsClientID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.wsClientID);
	}
}
