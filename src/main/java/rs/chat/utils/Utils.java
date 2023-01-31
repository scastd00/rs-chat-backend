package rs.chat.utils;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.tasks.Task;
import rs.chat.tasks.TaskExecutionException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

import static rs.chat.utils.Constants.GSON;

/**
 * Utility class for common operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
	private static final ExecutorService EXECUTOR_SERVICE;

	static {
		ThreadFactory threadFactory = r -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		};

		EXECUTOR_SERVICE = Executors.newCachedThreadPool(threadFactory);
	}

	public static void executeTask(Task task, Function<TaskExecutionException, Void> exceptionHandler) {
		EXECUTOR_SERVICE.execute(() -> {
			try {
				task.run();
			} catch (TaskExecutionException e) {
				exceptionHandler.apply(e);
			}
		});
	}

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
		return serverMessage(content, type, chatId).toString();
	}

	/**
	 * Creates a server message to send to the clients.
	 *
	 * @param content content of the message.
	 * @param type    type of the message.
	 * @param chatId  chatId to send the message to.
	 *
	 * @return the server message.
	 */
	private static Object serverMessage(String content, String type, String chatId) {
		return JsonMessageWrapper.builder()
		                         /* Headers */
		                         .username("Server")
		                         .chatId(chatId)
		                         .type(type)
		                         .date(System.currentTimeMillis())
		                         /* Body */
		                         .content(content)
		                         .build();
	}

	/**
	 * Determines if the running environment is DEVELOPMENT or PRODUCTION.
	 *
	 * @return {@code true} if the environment is DEVELOPMENT, {@code false} otherwise.
	 */
	public static boolean isDevEnv() {
		return System.getenv("ENV").toLowerCase().startsWith("dev");
	}

	/**
	 * Determines if the running environment is PRODUCTION.
	 *
	 * @return {@code true} if the environment is PRODUCTION, {@code false} otherwise.
	 */
	public static boolean isProdEnv() {
		return System.getenv("ENV").toLowerCase().startsWith("prod");
	}

	/**
	 * Determines if the running environment is inside a Docker container.
	 *
	 * @return {@code true} if the environment is inside a Docker container, {@code false} otherwise.
	 */
	public static boolean isDockerEnv() {
		return System.getenv("DOCKER").equals("true");
	}

	/**
	 * Adds a {@link Number} to a {@link JsonObject} and returns the {@link String} representation.
	 *
	 * @param key   the key of the {@link Number} to add.
	 * @param value the value of the {@link Number} to add.
	 *
	 * @return the {@link String} representation of the {@link JsonObject}.
	 */
	public static String jsonOfNumber(String key, Number value) {
		JsonObject json = new JsonObject();
		json.addProperty(key, value);
		return json.toString();
	}

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
