package rs.chat.net.ws;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;

import static rs.chat.utils.Constants.CHAT_FILES_PATH;

@Getter
public class Chat {
	private final CopyOnWriteArrayList<WSClient> clients;
	private final PrintWriter writer;

	@SneakyThrows
	public Chat(String chatId) {
		this.clients = new CopyOnWriteArrayList<>();

		FileWriter fileWriter = new FileWriter(CHAT_FILES_PATH + chatId + ".txt", true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		this.writer = new PrintWriter(bufferedWriter);
	}

	public void saveMessageToChatFile(String message) {
		this.writer.println(message);
	}

	public void finish() {
		this.writer.close();
	}
}
