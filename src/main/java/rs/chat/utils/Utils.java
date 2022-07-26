package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.WebSocketMessageType.SERVER_INFO_MESSAGE;
import static rs.chat.utils.Constants.ALGORITHM;
import static rs.chat.utils.Constants.GSON;

public final class Utils {
	private Utils() {
	}

	public static JsonObject parseJson(String jsonString) {
		// @formatter:off
		return GSON.fromJson(jsonString, new TypeToken<JsonObject>() {}.getType());
		// @formatter:on
	}

	public static Map<String, String> generateTokens(String username, HttpServletRequest request, String role) {
		Map<String, String> tokens = new HashMap<>();

		String accessToken = JWT.create()
		                        .withSubject(username)
		                        .withExpiresAt(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME))
		                        .withIssuer(request.getRequestURL().toString()) // URL of our application.
		                        .withClaim("role", role) // Only one role is in DB.
		                        .sign(ALGORITHM);

		String refreshToken = JWT.create()
		                         .withSubject(username)
		                         .withExpiresAt(new Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_EXPIRATION_TIME))
		                         .withIssuer(request.getRequestURL().toString()) // URL of our application.
		                         .sign(ALGORITHM);

		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);

		return tokens;
	}

	public static String shortJsonString(String key, String value) {
		return GSON.toJson(Map.of(key, value));
	}

	public static String createServerMessage(String message) {
		return createMessage("Server", "ALL", -1, SERVER_INFO_MESSAGE, null, "UTF-8", message);
	}

	public static String createMessage(String username, String chatId, long sessionId, String type,
	                                   String token, String encoding, String content) {
		JsonObject headers = new JsonObject();
		headers.add("username", new JsonPrimitive(username));
		headers.add("chatId", new JsonPrimitive(chatId));
		headers.add("sessionId", new JsonPrimitive(sessionId));
		headers.add("type", new JsonPrimitive(type));
		headers.add("token", token == null ? JsonNull.INSTANCE : new JsonPrimitive(token));

		JsonObject body = new JsonObject();
		body.add("encoding", new JsonPrimitive(encoding));
		body.add("content", new JsonPrimitive(content));

		JsonObject fullMessage = new JsonObject();
		fullMessage.add("headers", headers);
		fullMessage.add("body", body);

		return fullMessage.toString();
	}
}
