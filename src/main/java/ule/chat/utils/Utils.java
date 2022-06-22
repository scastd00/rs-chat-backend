package ule.chat.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Utils {
	private Utils() {
	}

	public static JsonObject readJson(String jsonString) {
		// @formatter:off
		return Constants.GSON.fromJson(jsonString, new TypeToken<JsonObject>() {}.getType());
		// @formatter:on
	}

	public static void sendError(HttpServletResponse response, String message, HttpStatus status) throws IOException {
		Map<String, String> res = new HashMap<>();

		res.put("error_message", message);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(status.value());
		new ObjectMapper().writeValue(response.getWriter(), res);
	}

	public static void send(HttpServletResponse response, Object content, HttpStatus status) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(status.value());
		new ObjectMapper().writeValue(response.getWriter(), content);
	}
}
