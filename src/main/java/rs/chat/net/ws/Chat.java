package rs.chat.net.ws;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rs.chat.storage.S3;
import rs.chat.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Slf4j
public class Chat {
	private final String chatId;
	private final CopyOnWriteArrayList<WSClient> clients;
	private final PrintWriter writer;

	@SneakyThrows
	public Chat(String chatId) {
		this.chatId = chatId;
		this.clients = new CopyOnWriteArrayList<>();

		File downloadFile = S3.getInstance().downloadFile(chatId);

		FileWriter fileWriter = new FileWriter(downloadFile, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		this.writer = new PrintWriter(bufferedWriter, true); // Auto-Flush enabled
	}

	public void saveMessageToChatFile(String message) {
		this.writer.println(message); // Write a line and flush
	}

	public void finish() {
		this.writer.close();

		File chatFile = Utils.getChatFile(this.chatId);
		S3.getInstance().uploadFile(chatFile);

		log.info("Uploaded file");
	}
}
