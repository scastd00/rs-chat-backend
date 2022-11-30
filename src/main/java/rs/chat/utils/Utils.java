package rs.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.TokenValidationException;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.tasks.Task;
import rs.chat.tasks.TaskExecutionException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

import static rs.chat.net.ws.Message.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.utils.Constants.ALGORITHM;
import static rs.chat.utils.Constants.DOCKER_S3_ENDPOINT_URI_FOR_FILES;
import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static rs.chat.utils.Constants.JWT_VERIFIER;
import static rs.chat.utils.Constants.LOCAL_S3_ENDPOINT_URI_FOR_FILES;
import static rs.chat.utils.Constants.REMOTE_S3_ENDPOINT_URI_FOR_FILES;
import static rs.chat.utils.Constants.TOKEN_EXPIRATION_DURATION_EXTENDED;
import static rs.chat.utils.Constants.TOKEN_EXPIRATION_DURATION_NORMAL;

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
	 * Creates the token that is used to authenticate the user.
	 *
	 * @param username             the username of the user.
	 * @param requestURL           the URL of the request.
	 * @param role                 the role of the user.
	 * @param extendExpirationTime if the expiration time of the token should be extended.
	 *
	 * @return the tokens that are used to authenticate the user.
	 */
	public static String generateJWTToken(String username, String requestURL, String role, boolean extendExpirationTime) {
		return JWT.create()
		          .withSubject(username)
		          .withExpiresAt(Instant.now().plus(
				          extendExpirationTime ?
				          TOKEN_EXPIRATION_DURATION_EXTENDED :
				          TOKEN_EXPIRATION_DURATION_NORMAL
		          ))
		          .withIssuer(requestURL) // URL of our application.
		          .withClaim("role", role) // Only one role is in DB.
		          .sign(ALGORITHM);
	}

	/**
	 * Verifies an authorization token.
	 *
	 * @param token the token to verify.
	 *
	 * @return the decoded JWT token.
	 *
	 * @throws JWTVerificationException if the token is invalid.
	 */
	public static DecodedJWT checkAuthorizationToken(String token) throws JWTVerificationException {
		return JWT_VERIFIER.verify(token.replace(JWT_TOKEN_PREFIX, ""));
	}

	/**
	 * Creates a {@link String} message containing the active users given a
	 * {@link List<String>} of usernames.
	 *
	 * @param usernames the {@link List<String>} of usernames.
	 *
	 * @return the {@link String} message containing the active users.
	 */
	public static String createActiveUsersMessage(List<String> usernames) {
		JsonArray usersArray = new JsonArray();
		usernames.forEach(usersArray::add);
		return createMessage(usersArray.toString(), ACTIVE_USERS_MESSAGE.type(), "");
		// In the client the chatId is ignored, so we minimize the size of the message with an empty string.
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
		return (JsonMessageWrapper.builder()
		                          /* Headers */
		                          .username("Server")
		                          .chatId(chatId)
		                          .type(type)
		                          .date(System.currentTimeMillis())
		                          /* Body */
		                          .content(content)
		                          .build())
				/* JsonObject */
				.toString();
	}

	/**
	 * Creates a {@link String} message containing an error message.
	 *
	 * @param message the error message to send.
	 *
	 * @return the {@link String} message containing the error message.
	 */
	public static String createErrorMessage(String message) {
		return (JsonMessageWrapper.builder()
		                          /* Headers */
		                          .username("Server")
		                          .chatId("NONE")
		                          .type(ERROR_MESSAGE.type())
		                          .date(System.currentTimeMillis())
		                          /* Body */
		                          .content(message)
		                          .build())
				/* JsonObject */
				.toString();
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

	public static URI uploadedFileURI(String s3Key) {
		return getCurrentS3EndpointURI().resolve(s3Key);
	}

	private static URI getCurrentS3EndpointURI() {
		if (isDockerEnv()) {
			return DOCKER_S3_ENDPOINT_URI_FOR_FILES;
		}

		if (isProdEnv()) {
			return REMOTE_S3_ENDPOINT_URI_FOR_FILES;
		}

		return LOCAL_S3_ENDPOINT_URI_FOR_FILES;
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

	/**
	 * Checks if the token is valid before handling the message.
	 *
	 * @param token token to check.
	 *
	 * @throws WebSocketException       if token is null or empty.
	 * @throws TokenValidationException if token is invalid.
	 */
	public static void checkTokenValidity(String token) throws WebSocketException, TokenValidationException {
		if (token == null) {
			throw new WebSocketException("Token is null");
		}

		if (token.isEmpty()) {
			throw new WebSocketException("Token is empty");
		}

		try {
			checkAuthorizationToken(token);
		} catch (JWTVerificationException e) {
			if (token.replace(JWT_TOKEN_PREFIX, "").equals("empty")) {
				return; // Client connected to the server without a token (started the app but not connected to a chat).
			}

			throw new TokenValidationException(e.getMessage());
		}
	}

	public static void setSpringDatasourceURLIfDockerIsRunning() {
		String docker = System.getenv("DOCKER");

		if (docker == null || docker.equals("false")) {
			return;
		}

		System.setProperty("spring.datasource.url", "jdbc:mysql://rschat-db-host:3306/rs_chat");
	}
}
