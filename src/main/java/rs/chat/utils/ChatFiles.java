package rs.chat.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static rs.chat.utils.Constants.CHAT_FILES_PATH;

@Slf4j
public class ChatFiles {

	private ChatFiles() {
	}

	public static void writeMessage(String message, String chatId) {
		try (
				FileWriter f = new FileWriter(CHAT_FILES_PATH + chatId + ".txt", true);
				PrintWriter writer = new PrintWriter(new BufferedWriter(f))
		) {
			writer.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
