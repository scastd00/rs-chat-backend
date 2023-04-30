package rs.chat.json;

import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static rs.chat.Constants.GSON;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonGenerator {
	/**
	 * Creates a tree-like JSON structure from an {@link Object}.
	 *
	 * @param object the {@link Object} to create a tree-like JSON structure from.
	 *
	 * @return the tree-like JSON structure.
	 */
	public static JsonElement jsonTree(Object object) {
		return GSON.toJsonTree(object);
	}
}
