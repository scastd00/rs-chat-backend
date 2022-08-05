package rs.chat.net.ws;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that manages the chats to which the clients are connected.
 */
@NoArgsConstructor
public class WebSocketChatMap {
	/**
	 * Map to store each chat. The mapping key is the chatId.
	 */
	private final Map<String, Chat> chats = new HashMap<>();

	/**
	 * Creates a new chat ({@link Chat}) for the specified key.
	 *
	 * @param chatId key of the chat to create.
	 */
	private synchronized void createChat(String chatId) {
		this.chats.put(chatId, new Chat(chatId));
	}

	/**
	 * Checks if the specified chatId is a key of the chats map.
	 *
	 * @param chatId chat id to check.
	 *
	 * @return {@code true} if the chat exists, {@code false} otherwise.
	 */
	private synchronized boolean chatExists(String chatId) {
		return this.chats.containsKey(chatId);
	}

	/**
	 * Returns the clients of the chat identified by the chatId or an empty list if
	 * it does not exist. <b>Use with caution, if the returned list is
	 * an empty one, no elements will be stored in the real chat.</b>
	 *
	 * @param chatId id of the chat to get.
	 *
	 * @return the required chat or an empty list.
	 */
	@NotNull
	private synchronized List<WSClient> getClientsOf(String chatId) {
		return this.chatExists(chatId) ? this.chats.get(chatId).getClients() : List.of();
	}

	private void saveMessage(String chatId, String message) {
		if (this.chatExists(chatId)) {
			this.chats.get(chatId).saveMessageToChatFile(message);
		}
	}

	/**
	 * Returns the client that is stored in the chat and has the specified ID.
	 *
	 * @param clientID ID of the client.
	 *
	 * @return a {@link WSClient} if the user is in the chat,
	 * {@code null} otherwise (if the chat is empty or the user is disconnected).
	 */
	public synchronized WSClient getClient(WSClientID clientID) {
		return this.getClientsOf(clientID.chatId())
		           .stream()
		           .filter(client -> client.wsClientID().equals(clientID))
		           .findFirst()
		           .orElse(null);
	}

	/**
	 * Adds a client to the specified chat (stored in the {@code wsClientID} attribute
	 * of {@link WSClient}).
	 *
	 * @param client new client to add to the chat.
	 */
	public synchronized void addClientToChat(WSClient client) {
		String chatId = client.wsClientID().chatId();

		if (!this.chatExists(chatId)) {
			this.createChat(chatId);
		}

		this.getClientsOf(chatId).add(client);
	}

	/**
	 * Removes the client from the specified chat (stored in the {@code wsClientID} attribute
	 * of {@link WSClient}). If the resulting chat is empty, the mapping is
	 * removed (so {@link #chatExists(String)} and {@link Map#get(Object)} will
	 * return {@code false}).
	 * <p>
	 * Precondition: the client is connected to the chat (so {@link #getClientsOf(String)} will
	 * return a non-empty list).
	 *
	 * @param clientID id of the client to remove from the chat.
	 */
	public synchronized void removeClientFromChat(WSClientID clientID) {
		String chatId = clientID.chatId();

		// Remove the user from the chat.
		this.getClientsOf(chatId)
		    .removeIf(client -> client.wsClientID().equals(clientID));

		// Delete the chat and its entry in the map if there are no more clients connected to it.
		if (this.getClientsOf(chatId).isEmpty()) {
			this.chats.get(chatId).finish();
			this.chats.remove(chatId);
		}
	}

	/**
	 * Sends a message to all clients connected to a chat.
	 *
	 * @param chatId  chat id to which the message should be sent.
	 * @param message message to send.
	 */
	public synchronized void broadcastToSingleChat(String chatId, String message) {
		this.getClientsOf(chatId).forEach(client -> client.send(message));
		this.saveMessage(chatId, message);
	}

	/**
	 * Sends a message to all clients connected to the chat except from the
	 * client that sent the message.
	 *
	 * @param clientID client to "ignore".
	 * @param message  message to send.
	 */
	public synchronized void broadcastToSingleChatAndExcludeClient(WSClientID clientID, String message) {
		this.getClientsOf(clientID.chatId())
		    .stream()
		    .filter(client -> !client.wsClientID().equals(clientID))
		    .forEach(client -> client.send(message));

		this.saveMessage(clientID.chatId(), message);
	}

	/**
	 * Sends a message to all the opened chats.
	 * <p>
	 * The purpose is to send maintenance messages or special events that
	 * will occur in the application.
	 *
	 * @param message message to send.
	 */
	public void totalBroadcast(String message) {
		this.chats.values()
		          .forEach(chat -> chat.getClients().forEach(client -> client.send(message)));
	}
}
