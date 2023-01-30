package rs.chat.net.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rs.chat.observability.metrics.Metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Class that manages the chats to which the clients are connected.
 */
@RequiredArgsConstructor
@Slf4j
@Component
@EnableScheduling
public class ChatManagement {
	/**
	 * Map to store each chat. The mapping key is the chatId.
	 */
	private final Map<String, Chat> chats = new HashMap<>();
	private final Metrics metrics;

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
	 * Adds a client to the specified chat (id of the chat is stored in the
	 * {@code clientID} attribute of {@link Client}). If the chat does not exist,
	 * it is created.
	 *
	 * @param client new client to add to the chat.
	 */
	public synchronized void addClientToChat(Client client) {
		String chatId = client.clientID().chatId();

		if (!this.chatExists(chatId)) {
			this.chats.put(chatId, new Chat(chatId));
		}

		this.chats.get(chatId).addClient(client);
	}

	/**
	 * Removes a client from the chat. After removing the user, if the chat has no more clients connected
	 * to it, the chat is saved (see {@link Chat#finish()}) and deleted from the map.
	 *
	 * @param clientID id of the client to remove.
	 */
	public synchronized void removeClientFromChat(ClientID clientID) {
		String chatId = clientID.chatId();
		Chat chat = this.chats.get(chatId);

		if (chat.removeClient(clientID) && chat.hasNoAvailableClients()) {
			// Delete the chat and its entry in the map if there are no more
			// clients connected to it.
			chat.finish();
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
		this.chats.get(chatId).broadcastAndSave(message);
	}

	/**
	 * Sends a message to all clients connected to the chat except from the
	 * client that sent the message.
	 *
	 * @param message  message to send.
	 * @param clientID client to "ignore".
	 */
	public void broadcastToSingleChatAndExcludeClient(String message, ClientID clientID) {
		this.chats.get(clientID.chatId()).sendWithClientExclusionAndSave(message, clientID);
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
	 * Sends a message to a client that is mentioned in the message.
	 *
	 * @param chatId   id of the chat to which the message was sent.
	 * @param username username of the client to send the message to.
	 * @param message  message to send.
	 */
	public void mentionUser(String chatId, String username, String message) {
		this.chats.get(chatId).mention(message, username);
		this.metrics.incrementMentionedUsers();
	}

	/**
	 * Retrieves all the usernames of the clients connected to the given chat. The usernames
	 * are sorted.
	 *
	 * @param chatId id of the chat to get the usernames of.
	 *
	 * @return a sorted list of usernames.
	 */
	public List<String> getUsernamesOfChat(String chatId) {
		return this.chats.get(chatId).getUsernames();
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
		this.chats.values().forEach(Chat::saveToS3);
	}

	/**
	 * Deletes all the users that have had some error in the connection
	 * and the instance is null for some reason.
	 */
	@Scheduled(fixedRate = 3, initialDelay = 3, timeUnit = MINUTES)
	private void deleteUnwantedUsers() {
		this.chats.values().forEach(Chat::deleteUnwantedUsers);
	}
}
