package rs.chat.domain;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import rs.chat.domain.entity.Chat;
import rs.chat.utils.Utils;

import static rs.chat.utils.Constants.DEGREE_CHAT;
import static rs.chat.utils.Constants.DEGREE_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.GROUP_CHAT;
import static rs.chat.utils.Constants.GROUP_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.SUBJECT_CHAT;
import static rs.chat.utils.Constants.SUBJECT_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.USER_CHAT;
import static rs.chat.utils.Constants.USER_CHAT_S3_FOLDER_PREFIX;

public final class DomainUtils {
	private DomainUtils() {
	}

	/**
	 * Instantiates a new {@link Chat} object with the given parameters.
	 *
	 * @param name         the name of the chat.
	 * @param chatType     the type of the chat.
	 * @param s3ChatPrefix the prefix of the chat folder in S3.
	 *
	 * @return the new {@link Chat} object.
	 */
	@NotNull
	private static Chat createChat(String name, String chatType, String s3ChatPrefix) {
		return new Chat(
				null,
				name,
				chatType,
				s3ChatPrefix + name,
				Utils.jsonOfNumber("createdAt", System.currentTimeMillis()),
				RandomStringUtils.randomAlphanumeric(15)
		);
	}

	/**
	 * Returns a new {@link Chat} of <b>individual</b> type.
	 *
	 * @param name the name of the chat.
	 *
	 * @return the new {@link Chat} of <b>individual</b> type.
	 */
	public static Chat individualChat(String name) {
		return createChat(name, USER_CHAT, USER_CHAT_S3_FOLDER_PREFIX);
	}

	/**
	 * Returns a new {@link Chat} of <b>group</b> type.
	 *
	 * @param name the name of the chat.
	 *
	 * @return the new {@link Chat} of <b>group</b> type.
	 */
	public static Chat groupChat(String name) {
		return createChat(name, GROUP_CHAT, GROUP_CHAT_S3_FOLDER_PREFIX);
	}

	/**
	 * Returns a new {@link Chat} of <b>subject</b> type.
	 *
	 * @param name the name of the chat.
	 *
	 * @return the new {@link Chat} of <b>subject</b> type.
	 */
	public static Chat subjectChat(String name) {
		return createChat(name, SUBJECT_CHAT, SUBJECT_CHAT_S3_FOLDER_PREFIX);
	}

	/**
	 * Returns a new {@link Chat} of <b>degree</b> type.
	 *
	 * @param name the name of the chat.
	 *
	 * @return the new {@link Chat} of <b>degree</b> type.
	 */
	public static Chat degreeChat(String name) {
		return createChat(name, DEGREE_CHAT, DEGREE_CHAT_S3_FOLDER_PREFIX);
	}
}
