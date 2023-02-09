package rs.chat.net.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rs.chat.observability.metrics.Metrics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	private final Map<String, Chat> chats = new ConcurrentHashMap<>();
	private final Metrics metrics;

	/**
	 * Checks if the specified chatId is a key of the chats map.
	 *
	 * @param chatId chat id to check.
	 *
	 * @return {@code true} if the chat exists, {@code false} otherwise.
	 */
	private boolean chatExists(String chatId) {
		return this.chats.containsKey(chatId);
	}

	/**
	 * Adds a client to the specified chat (id of the chat is stored in the
	 * {@code clientID} attribute of {@link Client}). If the chat does not exist,
	 * it is created.
	 *
	 * @param client new client to add to the chat.
	 */
	public void addClientToChat(Client client) {
		String chatId = client.getClientID().chatId();

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
	public void removeClientFromChat(ClientID clientID) {
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
	 * Sends a message to all clients connected to a chat and <b>saves</b> the message to history
	 * file.
	 *
	 * @param chatId  chat id to which the message should be sent.
	 * @param message message to send.
	 */
	public void broadcastToSingleChatAndSave(String chatId, String message) {
		this.chats.get(chatId).broadcast(message, true);
	}

	/**
	 * Sends a message to all clients connected to a chat <b>without saving</b> the message to
	 * history file.
	 *
	 * @param chatId  chat id to which the message should be sent.
	 * @param message message to send.
	 */
	public void broadcastToSingleChatWithoutSaving(String chatId, String message) {
		this.chats.get(chatId).broadcast(message, false);
	}

	/**
	 * Sends a message to all clients connected to the chat except from the
	 * client that sent the message. The message <b>is saved</b> to history file.
	 *
	 * @param message  message to send.
	 * @param clientID client to "ignore".
	 */
	public void broadcastToSingleChatExcludeClientAndSave(String message, ClientID clientID) {
		this.chats.get(clientID.chatId()).sendWithClientExclusion(message, clientID, true);
	}

	/**
	 * Sends a message to all clients connected to the chat except from the
	 * client that sent the message. The message <b>is not saved</b> to history file.
	 *
	 * @param message  message to send.
	 * @param clientID client to "ignore".
	 */
	public void broadcastToSingleChatExcludeClientWithoutSaving(String message, ClientID clientID) {
		this.chats.get(clientID.chatId()).sendWithClientExclusion(message, clientID, false);
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
		this.chats.values().forEach(chat -> chat.broadcast(message, true));
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
	 * Retrieves all the usernames of the clients connected (and active) to the given chat.
	 * They are sorted alphabetically.
	 *
	 * @param chatId id of the chat to get the usernames of.
	 *
	 * @return a sorted list of usernames of the active clients.
	 */
	public List<String> getActiveUsernamesOfChat(String chatId) {
		return this.chats.get(chatId).getActiveUsernames();
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

	/**
	 * Sets the client as away.
	 *
	 * @param clientID id of the client to set as away.
	 */
	public void setClientAway(ClientID clientID) {
		this.chats.get(clientID.chatId()).setClientAway(clientID);
	}

	/**
	 * Sets the client as active.
	 *
	 * @param clientID id of the client to set as active.
	 */
	public void setClientActive(ClientID clientID) {
		this.chats.get(clientID.chatId()).setClientActive(clientID);
	}
}
