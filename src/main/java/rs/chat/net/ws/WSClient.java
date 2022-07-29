package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCode;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class WSClient {
	private final Session session;
	private final WSClientID wsClientID;

	public WSClient(Session session, WSClientID wsClientID) {
		this.session = session;
		this.wsClientID = wsClientID;
	}

	public WSClientID getWSClientID() {
		return this.wsClientID;
	}

	public void send(String message) {
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			log.error("Could not send message to client", e);
		}
	}

	public void close(CloseCode code, String message) {
		try {
			this.session.close(new CloseReason(code, message));
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
