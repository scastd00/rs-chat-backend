package rs.chat.net.ws;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import rs.chat.cache.CachedHistoryFile;
import rs.chat.cache.HistoryFilesCache;
import rs.chat.storage.S3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class that stores the clients in a {@link CopyOnWriteArrayList} and have
 * a {@link CachedHistoryFile} associated to a file to store all the messages received.
 * This allows to read and write to the file faster, since the file is cached in memory.
 */
@Getter
@Slf4j
public class Chat {
	private final String chatId;
	private final CopyOnWriteArrayList<Client> clients = new CopyOnWriteArrayList<>();
	private final CachedHistoryFile historyFile;

	/**
	 * Creates the chat with the specified chatId.
	 *
	 * @param chatId id of the chat that is created.
	 */
	public Chat(String chatId) {
		this.chatId = chatId;
		this.historyFile = HistoryFilesCache.INSTANCE.get(chatId);
	}

	/**
	 * Adds a new client to the chat.
	 *
	 * @param client client to add.
	 */
	public synchronized void addClient(Client client) {
		this.clients.add(client);
	}

	/**
	 * Sends a message to all the available clients in the chat.
	 *
	 * @param message message to send.
	 */
	public void broadcast(String message) {
		this.availableClientsStream()
		    .forEach(client -> client.send(message));
	}

	/**
	 * Checks if the chat has any available clients.
	 *
	 * @return {@code true} if the chat has any available clients, {@code false} otherwise.
	 */
	public synchronized boolean hasNoAvailableClients() {
		return this.availableClientsStream().findAny().isEmpty();
	}

	/**
	 * Removes the specified client from the chat (closing the connection before the removal).
	 * If the client is not in the chat, {@code false} is returned and nothing is done to the
	 * list of clients.
	 *
	 * @param clientID id of the client to remove.
	 *
	 * @return {@code true} if the client was removed, {@code false} otherwise.
	 */
	public synchronized boolean removeClient(ClientID clientID) {
		final int ignoredIndex = -1;
		final boolean[] removed = { false };

		return IntStream.range(0, this.clients.size())
		                .map(i -> {
			                Client client = this.clients.get(i);

			                if (!removed[0] && client.clientID().equals(clientID)) {
				                client.close();
				                this.clients.remove(i);
				                removed[0] = true;

				                return i;
			                }

			                return ignoredIndex;
		                })
		                .anyMatch(i -> i != ignoredIndex /* A user was removed */);
	}

	/**
	 * Writes the message to the file associated with this chat if it is not an activity message.
	 *
	 * @param message message to store in the file.
	 */
	public void saveMessageToHistoryFile(String message) {
		if (!Message.typeBelongsToGroup(JsonMessageWrapper.fromString(message).type(), Message.ACTIVITY_MESSAGES)) {
			this.historyFile.write(message);
		}
	}

	/**
	 * Broadcasts the message to all the connected clients and saves it to the history file.
	 *
	 * @param message message to broadcast and save.
	 */
	public void broadcastAndSave(String message) {
		this.broadcast(message);
		this.saveMessageToHistoryFile(message);
	}

	/**
	 * Sends the message to all the clients except the one with the specified clientID
	 * and saves it to the history file.
	 *
	 * @param message  message to send.
	 * @param clientID id of the client to exclude.
	 */
	public void sendWithClientExclusionAndSave(String message, ClientID clientID) {
		this.availableClientsStream()
		    .filter(client -> !client.clientID().equals(clientID))
		    .forEach(client -> client.send(message));
		this.saveMessageToHistoryFile(message);
	}

	/**
	 * Closes the writer and uploads the file to S3 bucket with all the messages stored.
	 * <p>
	 * This method is called when the last client of the chat has left.
	 */
	public synchronized void finish() {
		this.saveToS3();
		this.historyFile.close();
		HistoryFilesCache.INSTANCE.invalidate(this.chatId);
	}

	/**
	 * Uploads the file to S3 bucket with all the messages stored.
	 */
	public void saveToS3() {
		S3.getInstance().uploadHistoryFile(this.chatId);
		log.debug("Uploaded file of chat with id = {}", this.chatId);
	}

	/**
	 * Deletes the users that are null or cannot send messages.
	 */
	synchronized void deleteUnwantedUsers() {
		this.clients.removeIf(client -> client == null || !client.canSend());
	}

	/**
	 * @return {@link Stream} of all the available clients in the chat.
	 */
	@NotNull
	private Stream<Client> availableClientsStream() {
		return this.clients.stream().filter(client -> client != null && client.canSend());
	}

	/**
	 * Sends a mention message to the specified user.
	 *
	 * @param message  message to send.
	 * @param username username of the user to send the mention to.
	 */
	public void mention(String message, String username) {
		this.availableClientsStream()
		    .filter(client -> client.clientID().username().equals(username))
		    .forEach(client -> client.send(message));
	}

	/**
	 * @return {@link List} of all the usernames of the available clients in the chat.
	 */
	public List<String> getUsernames() {
		return this.availableClientsStream()
		           .map(client -> client.clientID().username())
		           .sorted(String::compareToIgnoreCase)
		           .toList();
	}
}
