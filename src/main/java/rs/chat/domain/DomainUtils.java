package rs.chat.domain;

import org.jetbrains.annotations.NotNull;
import rs.chat.utils.Utils;

import static rs.chat.utils.Constants.DEGREE_CHAT;
import static rs.chat.utils.Constants.DEGREE_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.GROUP_CHAT;
import static rs.chat.utils.Constants.GROUP_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.SUBJECT_CHAT;
import static rs.chat.utils.Constants.SUBJECT_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.USER_CHAT;
import static rs.chat.utils.Constants.USER_CHAT_S3_FOLDER_PREFIX;

public class DomainUtils {
	private DomainUtils() {
	}

	@NotNull
	private static Chat getChat(String name, String chatType, String chatPrefix) {
		return new Chat(
				null,
				name,
				chatType,
				chatPrefix + name,
				Utils.jsonOf("created", Long.toString(System.currentTimeMillis()))
		);
	}

	public static Chat individualChat(String name) {
		return getChat(name, USER_CHAT, USER_CHAT_S3_FOLDER_PREFIX);
	}

	public static Chat groupChat(String name) {
		return getChat(name, GROUP_CHAT, GROUP_CHAT_S3_FOLDER_PREFIX);
	}

	public static Chat subjectChat(String name) {
		return getChat(name, SUBJECT_CHAT, SUBJECT_CHAT_S3_FOLDER_PREFIX);
	}

	public static Chat degreeChat(String name) {
		return getChat(name, DEGREE_CHAT, DEGREE_CHAT_S3_FOLDER_PREFIX);
	}
}
