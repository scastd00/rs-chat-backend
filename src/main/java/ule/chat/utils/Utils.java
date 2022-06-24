package ule.chat.utils;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public final class Utils {
	private Utils() {
	}

	public static JsonObject readJson(String jsonString) {
		// @formatter:off
		return Constants.GSON.fromJson(jsonString, new TypeToken<JsonObject>() {}.getType());
		// @formatter:on
	}
}
