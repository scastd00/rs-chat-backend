package rs.chat.net.ws;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rs.chat.storage.S3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that stores the clients in a {@link CopyOnWriteArrayList<WSClient>} and have
 * a {@link PrintWriter} associated to a file to store all the messages received.
 */
@Getter
@Slf4j
public class Chat {
	private final String chatId;
	private final CopyOnWriteArrayList<WSClient> clients;
	private final PrintWriter writer;

	/**
	 * Creates the chat with the specified chatId.
	 *
	 * @param chatId id of the chat that is created.
	 */
	@SneakyThrows(IOException.class)
	public Chat(String chatId) {
		this.chatId = chatId;
		this.clients = new CopyOnWriteArrayList<>();

		File downloadFile = S3.getInstance().downloadHistoryFile(chatId);

		FileWriter fileWriter = new FileWriter(downloadFile, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		this.writer = new PrintWriter(bufferedWriter, true); // Auto-Flush enabled
	}

	/**
	 * Writes the message to the file associated with this chat.
	 *
	 * @param message message to store in the file.
	 */
	public void saveMessageToChatFile(String message) {
		this.writer.println(message); // Write a line and flush
	}

	/**
	 * Closes the writer and uploads the file to S3 bucket with all the messages stored.
	 * <p>
	 * This method is called when the last client of the chat has left.
	 */
	public void finish() {
		this.writer.close();
		this.saveToS3();
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
	public void deleteUnwantedUsers() {
		this.clients.removeIf(client -> client == null || client.session() == null || !client.session().isOpen());
	}
}
