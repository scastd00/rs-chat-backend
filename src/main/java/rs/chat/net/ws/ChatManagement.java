package rs.chat.net.ws;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Class that manages the chats to which the clients are connected.
 */
@NoArgsConstructor
@Slf4j
@Component
@EnableScheduling
public class ChatManagement {
	/**
	 * Map to store each chat. The mapping key is the chatId.
	 */
	private final Map<String, Chat> chats = new HashMap<>();

	/**
	 * Creates a new {@link Chat} for the specified key.
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
	 * it does not exist. <b>Use with caution, if the returned list is an empty one
	 * (the chat does not exist), no elements will be stored in the real chat.</b>
	 *
	 * @param chatId id of the chat to get.
	 *
	 * @return the required chat or an empty list.
	 */
	@NotNull
	private synchronized List<Client> getClientsOf(String chatId) {
		return this.chatExists(chatId) ? this.chats.get(chatId).getClients() : List.of();
	}

	/**
	 * Writes the message to the corresponding chat file if it exists.
	 *
	 * @param chatId  id of the chat and the file to write to.
	 * @param message message to write.
	 */
	private synchronized void saveMessage(String chatId, String message) {
		if (this.chatExists(chatId)) {
			this.chats.get(chatId).saveMessageToHistoryFile(message);
		}
	}

	/**
	 * Returns the client that is stored in the chat and has the specified ID.
	 *
	 * @param clientID ID of the client.
	 *
	 * @return a {@link Client} if the user is in the chat,
	 * {@code null} otherwise (if the chat is empty or the user is disconnected).
	 */
	public synchronized Client getClient(ClientID clientID) {
		return this.getClientsOf(clientID.chatId())
		           .stream()
		           .filter(client -> client.clientID().equals(clientID))
		           .findFirst()
		           .orElse(null);
	}

	/**
	 * Adds a client to the specified chat (id of the chat is stored in the
	 * {@code clientID} attribute of {@link Client}).
	 *
	 * @param client new client to add to the chat.
	 */
	public synchronized void addClientToChat(Client client) {
		String chatId = client.clientID().chatId();

		if (!this.chatExists(chatId)) {
			this.createChat(chatId);
		}

		this.getClientsOf(chatId).add(client);
	}

	/**
	 * Removes the client from the specified chat (id of the chat is stored in the
	 * {@code clientID} attribute of {@link Client}). If the resulting chat is
	 * empty, the mapping is removed (so {@link #chatExists(String)} and
	 * {@link Map#get(Object)} will return {@code false}) and the chat is finished {@link Chat#finish()}.
	 * <p>
	 * Precondition: the client is connected to the chat (so {@link #getClientsOf(String)} will
	 * return a non-empty list).
	 *
	 * @param clientID id of the client to remove from the chat.
	 */
	public synchronized void removeClientFromChat(ClientID clientID) {
		String chatId = clientID.chatId();

		// Remove the user from the chat.
		List<Client> clientsOfChat = this.getClientsOf(chatId);
		clientsOfChat.removeIf(client -> client == null || client.clientID().equals(clientID));

		// Delete the chat and its entry in the map if there are no more
		// clients connected to it.
		if (clientsOfChat.isEmpty()) {
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
		this.chats.get(chatId).broadcastAndSave(message);
	}

	/**
	 * Sends a message to all clients connected to the chat except from the
	 * client that sent the message.
	 *
	 * @param clientID client to "ignore".
	 * @param message  message to send.
	 */
	public synchronized void broadcastToSingleChatAndExcludeClient(ClientID clientID, String message) {
		this.getClientsOf(clientID.chatId())
		    .stream()
		    .filter(client -> !client.clientID().equals(clientID))
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
		this.chats.values().forEach(chat -> chat.broadcastAndSave(message));
	}

	/**
	 * Retrieves all the usernames of the clients connected to the given chat.
	 *
	 * @param chatId id of the chat to get the usernames of.
	 *
	 * @return a list of usernames.
	 */
	public List<String> getUsernamesOfChat(String chatId) {
		return this.getClientsOf(chatId)
		           .stream()
		           .map(client -> client.clientID().username())
		           .toList();
	}

	/**
	 * Closes all the chats, writing to disk the messages that have not been written and
	 * sends the history to S3 bucket.
	 */
	public void close() {
		this.chats.values().forEach(Chat::finish);
	}

	/**
	 * Saves all chat files to S3 bucket every 10 minutes (to avoid data loss).
	 */
	@Scheduled(fixedRate = 10, initialDelay = 10, timeUnit = MINUTES)
	private void saveAllToS3() {
		log.debug("Saving all chats to S3");
		this.chats.values().forEach(Chat::saveToS3);
	}

	/**
	 * Deletes all the users that have had some error in the connection
	 * and the instance is null for some reason.
	 */
	@Scheduled(fixedRate = 3, initialDelay = 3, timeUnit = MINUTES)
	private void deleteNullUsers() {
		log.debug("Deleting null users of all chats");
		this.chats.values().forEach(Chat::deleteUnwantedUsers);
	}
}