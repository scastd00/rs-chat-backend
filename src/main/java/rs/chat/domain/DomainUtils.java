package rs.chat.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import rs.chat.domain.entity.Chat;
import rs.chat.utils.Utils;

import java.util.LinkedHashSet;

import static rs.chat.utils.Constants.CHAT_KEY_FORMAT;
import static rs.chat.utils.Constants.DEGREE;
import static rs.chat.utils.Constants.DEGREE_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.GROUP;
import static rs.chat.utils.Constants.GROUP_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.SUBJECT;
import static rs.chat.utils.Constants.SUBJECT_CHAT_S3_FOLDER_PREFIX;
import static rs.chat.utils.Constants.USER;
import static rs.chat.utils.Constants.USER_CHAT_S3_FOLDER_PREFIX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DomainUtils {
	/**
	 * Instantiates a new {@link Chat} object with the given parameters.
	 *
	 * @param name         the name of the chat.
	 * @param chatType     the type of the chat.
	 * @param s3ChatPrefix the prefix of the chat folder in S3.
	 * @param entityId     the id of the created entity.
	 *
	 * @return the new {@link Chat} object.
	 */
	@NotNull
	private static Chat createChat(String name, String chatType, String s3ChatPrefix, String entityId) {
		return new Chat(
				null,
				name,
				chatType,
				s3ChatPrefix + name,
				Utils.jsonOfNumber("createdAt", System.currentTimeMillis()),
				RandomStringUtils.randomAlphanumeric(15),
				getChatKey(chatType, entityId),
				new LinkedHashSet<>()
		);
	}

	/**
	 * Key of the chat in the database by using the chat type and the id of the entity.
	 *
	 * @param chatType the type of the chat.
	 * @param entityId the id of the entity.
	 *
	 * @return the key of the chat.
	 */
	public static String getChatKey(String chatType, String entityId) {
		return CHAT_KEY_FORMAT.formatted(chatType, entityId);
	}

	/**
	 * Returns a new {@link Chat} of <b>individual</b> type.
	 *
	 * @param name     the name of the chat.
	 * @param entityId the id of the created entity.
	 *
	 * @return the new {@link Chat} of <b>individual</b> type.
	 */
	public static Chat individualChat(String name, String entityId) {
		//! Caution with entityId: it can follow the structure: "user-userId1_userId2"
		//! When going to remove this type of chat, the order of the ids is not important, because it is like a set.
		return createChat(name, USER, USER_CHAT_S3_FOLDER_PREFIX, entityId);
	}

	/**
	 * Returns a new {@link Chat} of <b>group</b> type.
	 *
	 * @param name     the name of the chat.
	 * @param entityId the id of the created entity.
	 *
	 * @return the new {@link Chat} of <b>group</b> type.
	 */
	public static Chat groupChat(String name, Long entityId) {
		return createChat(name, GROUP, GROUP_CHAT_S3_FOLDER_PREFIX, entityId.toString());
	}

	/**
	 * Returns a new {@link Chat} of <b>subject</b> type.
	 *
	 * @param name     the name of the chat.
	 * @param entityId the id of the created entity.
	 *
	 * @return the new {@link Chat} of <b>subject</b> type.
	 */
	public static Chat subjectChat(String name, Long entityId) {
		return createChat(name, SUBJECT, SUBJECT_CHAT_S3_FOLDER_PREFIX, entityId.toString());
	}

	/**
	 * Returns a new {@link Chat} of <b>degree</b> type.
	 *
	 * @param name     the name of the chat.
	 * @param entityId the id of the created entity.
	 *
	 * @return the new {@link Chat} of <b>degree</b> type.
	 */
	public static Chat degreeChat(String name, Long entityId) {
		return createChat(name, DEGREE, DEGREE_CHAT_S3_FOLDER_PREFIX, entityId.toString());
	}
}
