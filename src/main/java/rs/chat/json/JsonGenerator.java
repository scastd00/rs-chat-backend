package rs.chat.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.JsonParseException;

import java.io.IOException;
import java.io.Writer;

import static rs.chat.json.JsonConstants.GSON;
import static rs.chat.json.JsonConstants.JACKSON;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonGenerator {
	/**
	 * Creates a tree-like JSON structure from an {@link Object}.
	 *
	 * @param object the {@link Object} to create a tree-like JSON structure from.
	 *
	 * @return the tree-like JSON structure.
	 */
	public static JsonElement gsonJsonTree(Object object) {
		return GSON.toJsonTree(object);
	}

	/**
	 * Creates a JSON string from an {@link Object} using Jackson.
	 *
	 * @param object the {@link Object} to create a JSON string from.
	 *
	 * @return the JSON string.
	 */
	public static String jacksonString(Object object) {
		try {
			return JACKSON.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new JsonParseException(e.getMessage());
		}
	}

	/**
	 * Writes a JSON string to a {@link Writer} using Jackson.
	 *
	 * @param writer the {@link Writer} to write the JSON string to.
	 * @param object the {@link Object} to create a JSON string from.
	 */
	public static void jacksonWrite(Writer writer, Object object) {
		try {
			JACKSON.writeValue(writer, object);
		} catch (IOException e) {
			throw new JsonParseException(e.getMessage());
		}
	}
}
