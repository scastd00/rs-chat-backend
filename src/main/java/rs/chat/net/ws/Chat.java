package rs.chat.net.ws;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import rs.chat.cache.CachedHistoryFile;
import rs.chat.cache.HistoryFilesCache;
import rs.chat.storage.S3;

import java.util.List;
import java.util.Optional;
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
	public void addClient(Client client) {
		this.clients.add(client);
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
	 * Removes the specified client from the chat (<b>without</b> closing the connection).
	 * If the client is not in the chat, {@code false} is returned and nothing is done to the
	 * list of clients.
	 *
	 * @param clientID id of the client to remove.
	 *
	 * @return {@code true} if the client was removed, {@code false} otherwise.
	 */
	public boolean removeClient(ClientID clientID) {
		final int ignoredIndex = -1;
		final boolean[] removed = { false };

		return IntStream.range(0, this.clients.size())
		                .map(i -> {
			                Client client = this.clients.get(i);

			                if (!removed[0] && client.getClientID().equals(clientID)) {
				                this.clients.remove(i);
				                removed[0] = true;

				                return i;
			                }

			                return ignoredIndex;
		                })
		                .anyMatch(i -> i != ignoredIndex /* A user was removed */);
	}

	/**
	 * Writes the message to the file associated with this chat.
	 *
	 * @param message message to store in the file.
	 */
	public void saveMessageToHistoryFile(String message) {
		this.historyFile.write(message);
	}

	/**
	 * Broadcasts the message to all the connected clients. It is saved to the history file
	 * if the specified parameter is {@code true}.
	 *
	 * @param message message to broadcast.
	 * @param save    {@code true} if the message should be saved to the history file, {@code false} otherwise.
	 */
	public void broadcast(String message, boolean save) {
		this.availableClientsStream().forEach(client -> client.send(message));
		if (save) this.saveMessageToHistoryFile(message);
	}

	/**
	 * Sends the message to all the clients except the one with the specified clientID.
	 * The message is saved to the history file if the specified parameter is {@code true}.
	 *
	 * @param message  message to send.
	 * @param clientID id of the client to exclude.
	 * @param save     {@code true} if the message should be saved to the history file, {@code false} otherwise.
	 */
	public void sendWithClientExclusion(String message, ClientID clientID, boolean save) {
		this.availableClientsStream()
		    .filter(client -> !client.getClientID().equals(clientID))
		    .forEach(client -> client.send(message));
		if (save) this.saveMessageToHistoryFile(message);
	}

	/**
	 * Closes the writer and uploads the file to S3 bucket with all the messages stored.
	 * <p>
	 * This method is called when the last client of the chat has left.
	 */
	public synchronized void finish() {
		this.historyFile.close();
		HistoryFilesCache.INSTANCE.invalidate(this.chatId);
		this.clients.clear();
	}

	/**
	 * Uploads the file to S3 bucket with all the messages stored. The file stored on disk
	 * is not deleted.
	 */
	public void saveToS3() {
		S3.getInstance().uploadHistoryFile(this.chatId, false);
	}

	/**
	 * Deletes the users that are null or cannot send messages.
	 */
	void deleteUnwantedUsers() {
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
		    .filter(client -> client.getClientID().username().equals(username))
		    .findFirst()
		    .ifPresent(client -> client.send(message));
	}

	/**
	 * @return {@link List} of all the usernames of the available and active clients in the chat sorted
	 * alphabetically.
	 */
	public List<String> getActiveUsernames() {
		return this.availableClientsStream()
		           .filter(Client::isActive)
		           .map(client -> client.getClientID().username())
		           .sorted(String::compareToIgnoreCase)
		           .toList();
	}

	/**
	 * Sets the specified client as away.
	 *
	 * @param clientID id of the client to set as away.
	 */
	public void setClientAway(ClientID clientID) {
		searchClient(clientID).ifPresent(client -> client.setAway(true));
	}

	/**
	 * Sets the specified client as active.
	 *
	 * @param clientID id of the client to set as active.
	 */
	public void setClientActive(ClientID clientID) {
		searchClient(clientID).ifPresent(client -> client.setAway(false));
	}

	/**
	 * Searches for the client with the specified clientID.
	 *
	 * @param clientID id of the client to search for.
	 *
	 * @return {@link Optional} of the client with the specified clientID.
	 */
	private Optional<Client> searchClient(ClientID clientID) {
		return this.availableClientsStream()
		           .filter(client -> client.getClientID().equals(clientID))
		           .findFirst();
	}
}
