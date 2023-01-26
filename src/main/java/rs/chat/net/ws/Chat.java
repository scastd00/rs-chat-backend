package rs.chat.net.ws;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rs.chat.cache.CachedHistoryFile;
import rs.chat.cache.HistoryFilesCache;
import rs.chat.storage.S3;

import java.util.concurrent.CopyOnWriteArrayList;

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

	public void broadcast(String message) {
		this.clients.forEach(client -> client.send(message));
	}

	/**
	 * Writes the message to the file associated with this chat.
	 *
	 * @param message message to store in the file.
	 */
	public void saveMessageToHistoryFile(String message) {
		this.historyFile.write(message);
	}

	public void broadcastAndSave(String message) {
		this.broadcast(message);
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
	 * Deletes the users that are null or their connection is nonexistent, or it is closed.
	 */
	synchronized void deleteUnwantedUsers() {
		this.clients.removeIf(client -> client == null || client.session() == null || !client.session().isOpen());
	}
}
