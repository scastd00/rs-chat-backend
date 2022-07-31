package rs.chat.storage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static rs.chat.utils.Constants.CHAT_FILES_PATH;

public class MessageWriter {
	private final PrintWriter printWriter;

	public MessageWriter(String chatId) throws IOException {
		FileWriter fileWriter = new FileWriter(CHAT_FILES_PATH + chatId + ".txt", true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		this.printWriter = new PrintWriter(bufferedWriter);
	}

	public void writeMessage(String message) {
		this.printWriter.println(message);
	}

	public void close() {
		this.printWriter.close();
	}
}
