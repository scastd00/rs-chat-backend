package rs.chat.net.ws;

/**
 * Creates the ID for a WebSocket client.
 *
 * @param username  username of the client.
 * @param chatId    id of the chat to which the client is connecting/connected/disconnecting.
 * @param sessionId id of the session that the user has in frontend.
 */
public record WSClientID(String username, String chatId, long sessionId) {
}
