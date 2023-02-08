package rs.chat.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.net.ws.JsonMessageWrapper;

import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Constants.OBJECT_MAPPER;

/**
 * Utility class for common operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
	/**
	 * Parses a JSON string into a {@link JsonObject}.
	 *
	 * @param jsonString the JSON string to parse into a {@link JsonObject}.
	 *
	 * @return the {@link JsonObject} parsed from the JSON string.
	 */
	public static JsonObject parseJson(String jsonString) {
		return GSON.fromJson(jsonString, JsonObject.class);
	}

	/**
	 * Creates a {@link String} message containing a server message.
	 *
	 * @param content the message to send.
	 * @param type    the type of the message.
	 * @param chatId  the chatId to send the message to.
	 *
	 * @return the {@link String} message containing the server message.
	 */
	public static String createMessage(String content, String type, String chatId) {
		try {
			return OBJECT_MAPPER.writeValueAsString(
					JsonMessageWrapper.builder()
					                  /* Headers */
					                  .username("Server")
					                  .chatId(chatId)
					                  .type(type)
					                  .date(System.currentTimeMillis())
					                  /* Body */
					                  .content(content)
					                  .build()
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converts bytes to a human-readable unit.
	 *
	 * @param bytes the bytes to convert.
	 *
	 * @return the human-readable unit.
	 */
	public static String bytesToUnit(int bytes) {
		if (bytes < 1024) {
			return bytes + " B";
		} else if (bytes < 1024 * 1024) {
			return String.format("%.2f", bytes / 1024d) + " KB";
		} else if (bytes < 1024 * 1024 * 1024) {
			return String.format("%.2f", bytes / (1024d * 1024)) + " MB";
		} else {
			return String.format("%.2f", bytes / (1024d * 1024 * 1024)) + " GB";
		}
	}
}
