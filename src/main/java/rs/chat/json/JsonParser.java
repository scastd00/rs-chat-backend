package rs.chat.json;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static rs.chat.json.JsonConstants.GSON;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonParser {
	/**
	 * Parses a JSON string into a {@link JsonObject}.
	 *
	 * @param jsonString the JSON string to parse into a {@link JsonObject}.
	 *
	 * @return the {@link JsonObject} parsed from the JSON string.
	 */
	public static JsonObject gsonParse(String jsonString) {
		return GSON.fromJson(jsonString, JsonObject.class);
	}
}
