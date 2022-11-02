package rs.chat.net.ws;

import java.util.Objects;

/**
 * Creates the ID for a WebSocket client.
 *
 * @param username  username of the client.
 * @param chatId    id of the chat to which the client is connecting/connected/disconnecting.
 * @param sessionId id of the session that the user has in frontend.
 */
public record ClientID(String username, String chatId, long sessionId) {
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClientID that = (ClientID) o;
		return username.equals(that.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
}
