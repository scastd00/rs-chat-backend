package rs.chat.net.ws;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that manages the chats to which the clients connect.
 */
@NoArgsConstructor
public class WebSocketChatMap {
	private final Map<String, CopyOnWriteArrayList<RSChatWebSocketClient>> chats = new HashMap<>();

	/**
	 * Creates a new {@link CopyOnWriteArrayList} for the specified key.
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
	 * @param chatId chatId of the chat to get.
	 *
	 * @return the required chat or an empty list.
	 */
	@NotNull
	private synchronized List<RSChatWebSocketClient> get(String chatId) {
		return this.chatExists(chatId) ? this.chats.get(chatId) : List.of();
	}

	/**
	 * Returns the client that is stored in the chat and has the specified
	 * username and session id.
	 *
	 * @param chatId    chat where the user is to be found.
	 * @param username  username of the user we want to get.
	 * @param sessionId session id of the user we want to get.
	 *
	 * @return a {@link RSChatWebSocketClient} if the user is in the chat,
	 * {@code null} otherwise (if the chat is empty or the user is disconnected).
	 */
	public synchronized RSChatWebSocketClient getClientByUsernameAndDate(String chatId,
	                                                                     String username,
	                                                                     long sessionId) {
		List<RSChatWebSocketClient> clientFoundList =
				this.get(chatId)
				    .stream()
				    .filter(client -> client.getUsername().equals(username))
				    .toList();

		return clientFoundList.isEmpty() ? null : clientFoundList.get(0);
	}

	/**
	 * Adds a client to the specified chat (stored in the {@code chatId} attribute
	 * of {@link RSChatWebSocketClient}).
	 *
	 * @param client new client to add to the chat.
	 */
	public synchronized void addClientToChat(RSChatWebSocketClient client) {
		String chatId = client.getChatId();

		if (!this.chatExists(chatId)) {
			this.createChat(chatId);
		}

		this.get(chatId).add(client);
	}

	/**
	 * Removes the client from the specified chat (stored in the {@code chatId} attribute
	 * of {@link RSChatWebSocketClient}). If the resulting chat is empty, the mapping is
	 * removed (so {@link #chatExists(String)} and {@link Map#get(Object)} will
	 * return {@code false}).
	 * <p>
	 * Precondition: the client is connected to the chat (so {@link #get(String)} will
	 * return a non-empty list).
	 *
	 * @param client client to remove from the chat.
	 */
	public synchronized void removeClientFromChat(RSChatWebSocketClient client) {
		String chatId = client.getChatId();
		String username = client.getUsername();

		this.get(chatId).remove(client);

		if (this.get(chatId).isEmpty()) {
			// Delete the entry of the chat since there are no more clients in it.
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
	 * @param chatId  chat id to which the message should be sent.
	 * @param message message to send.
	 * @param client  client to "ignore".
	 */
	public void broadcastToSingleChatAndExcludeClient(String chatId,
	                                                  String message,
	                                                  RSChatWebSocketClient client) {
		this.get(chatId)
		    .stream()
		    .filter(c -> !c.equals(client))
		    .forEach(c -> c.send(message));
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
