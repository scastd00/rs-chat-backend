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

import static rs.chat.net.ws.WSMessage.TEXT_MESSAGE;

/**
 * Chat that stores the clients and have a Writer associated to a file
 * to store all the messages received.
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

		File downloadFile = S3.getInstance().downloadFile(chatId, TEXT_MESSAGE);

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
	 * Closes the writer and uploads the file with all the messages stored.
	 * <p>
	 * This method is called when the last client of the chat has left.
	 */
	public void finish() {
		this.writer.close();

		S3.getInstance().uploadFile(this.chatId, TEXT_MESSAGE);

		log.debug("Uploaded file");
	}
}
