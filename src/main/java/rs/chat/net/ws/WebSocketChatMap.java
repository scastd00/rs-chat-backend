package rs.chat.net.ws;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that manages the chats to which the clients are connected.
 */
@NoArgsConstructor
public class WebSocketChatMap {
	/**
	 * Map to store each chat. The mapping key is the chatId.
	 */
	private final Map<String, CopyOnWriteArrayList<WSClient>> chats = new HashMap<>();

	/**
	 * Creates a new chat ({@link CopyOnWriteArrayList}) for the specified key.
	 *
	 * @param chatId key of the chat to create.
	 */
	private synchronized void createChat(String chatId) {
		this.chats.put(chatId, new CopyOnWriteArrayList<>());
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
	 * Returns the chat identified by the chatId or an empty list if
	 * it does not exist. <b>Use with caution, if the returned list is
	 * an empty one, no elements will be stored in the real chat.</b>
	 *
	 * @param chatId id of the chat to get.
	 *
	 * @return the required chat or an empty list.
	 */
	@NotNull
	private synchronized List<WSClient> get(String chatId) {
		return this.chatExists(chatId) ? this.chats.get(chatId) : List.of();
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
		List<WSClient> clientFoundList =
				this.get(clientID.chatId())
				    .stream()
				    .filter(client -> client.getWSClientID().equals(clientID))
				    .toList();

		return clientFoundList.isEmpty() ? null : clientFoundList.get(0);
	}

	/**
	 * Adds a client to the specified chat (stored in the {@code wsClientID} attribute
	 * of {@link WSClient}).
	 *
	 * @param client new client to add to the chat.
	 */
	public synchronized void addClientToChat(WSClient client) {
		String chatId = client.getWSClientID().chatId();

		if (!this.chatExists(chatId)) {
			this.createChat(chatId);
		}

		this.get(chatId).add(client);
	}

	/**
	 * Removes the client from the specified chat (stored in the {@code wsClientID} attribute
	 * of {@link WSClient}). If the resulting chat is empty, the mapping is
	 * removed (so {@link #chatExists(String)} and {@link Map#get(Object)} will
	 * return {@code false}).
	 * <p>
	 * Precondition: the client is connected to the chat (so {@link #get(String)} will
	 * return a non-empty list).
	 *
	 * @param clientID client to remove from the chat.
	 */
	public synchronized void removeClientFromChat(WSClientID clientID) {
		String chatId = clientID.chatId();

		// Remove the user from the chat.
		this.get(chatId).removeIf(client -> client.getWSClientID().equals(clientID));

		if (this.get(chatId).isEmpty()) {
			// Delete the entry of the chat if there are no more clients connected to it.
			this.chats.remove(chatId);
		}
	}

	/**
	 * Sends a message to all clients connected to a chat.
	 *
	 * @param chatId  chat id to which the message should be sent.
	 * @param message message to send.
	 */
	public void broadcastToSingleChat(String chatId, String message) {
		this.get(chatId).forEach(client -> client.send(message));
	}

	/**
	 * Sends a message to all clients connected to the chat except from the
	 * client that sent the message.
	 *
	 * @param clientID  client to "ignore".
	 * @param message message to send.
	 */
	public void broadcastToSingleChatAndExcludeClient(WSClientID clientID, String message) {
		this.get(clientID.chatId())
		    .stream()
		    .filter(client -> !client.getWSClientID().equals(clientID))
		    .forEach(client -> client.send(message));
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
		          .forEach(chat -> chat.forEach(client -> client.send(message)));
	}
}
