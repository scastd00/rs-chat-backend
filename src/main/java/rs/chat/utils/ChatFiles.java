package rs.chat.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static rs.chat.utils.Constants.CHAT_FILES_PATH;

@Slf4j
public class ChatFiles {

	private static final String FILE = CHAT_FILES_PATH + "Test.txt";

	private ChatFiles() {
	}

	public static void writeMessage(String message) {
		try (
				FileWriter f = new FileWriter(FILE, true);
				PrintWriter writer = new PrintWriter(new BufferedWriter(f))
		) {
			writer.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
