package rs.chat.net.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.chat.json.JsonParser;
import rs.chat.builder.Builder;

import static rs.chat.Constants.OBJECT_MAPPER;
import static rs.chat.net.ws.Message.MEDIA_MESSAGES;

/**
 * Class that wraps the received JSON messages. They follow the next structure:
 * <pre>{@code
 * {
 *  "headers": {
 *    "username": "<username>",
 *    "chatId": "<chatId>",
 *    "sessionId": "<sessionId>",
 *    "type": "<typeConstant>",
 *    "date": "<currentDate>",
 *    "token": "Bearer <token>"
 *  },
 *  "body": {
 *    "content": "<contentOfMessage>"
 *  }
 * }
 * }</pre>
 */
@Getter
@Slf4j
public class JsonMessageWrapper {
	private final String rawPayload;
	private final JsonObject parsedPayload;

	/**
	 * Constructor that parses the received JSON message.
	 *
	 * @param rawPayload the received JSON message as a string.
	 */
	public JsonMessageWrapper(String rawPayload) {
		this.rawPayload = rawPayload;
		this.parsedPayload = JsonParser.parseJson(rawPayload);
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
					builder()
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
	 * @return the headers of the message.
	 */
	public JsonObject headers() {
		return (JsonObject) this.parsedPayload.get("headers");
	}

	/**
	 * @return the body of the message.
	 */
	public JsonObject body() {
		return (JsonObject) this.parsedPayload.get("body");
	}

	/**
	 * @return the username of the message sender.
	 */
	public String username() {
		return this.headers().get("username").getAsString();
	}

	/**
	 * @return the chat id of the message sender.
	 */
	public String chatId() {
		return this.headers().get("chatId").getAsString();
	}

	/**
	 * @return the session id of the message sender.
	 */
	public long sessionId() {
		return this.headers().get("sessionId").getAsLong();
	}

	/**
	 * @return the type of the message.
	 */
	public String type() {
		return this.headers().get("type").getAsString();
	}

	/**
	 * @return the date of the message.
	 */
	public long date() {
		return this.headers().get("date").getAsLong();
	}

	/**
	 * @return the token of the message.
	 */
	public String token() {
		return this.headers().get("token").getAsString();
	}

	/**
	 * @return the content of the message.
	 */
	public String content() {
		return this.body().get("content").getAsString();
	}

	/**
	 * Updates the date of the message to the current date.
	 */
	public void updateDateTime() {
		this.headers().addProperty("date", System.currentTimeMillis());
	}

	public void setContent(String content) {
		this.body().addProperty("content", content);
	}

	/**
	 * @return the message as a string with the updated fields (if any).
	 */
	@Override
	public String toString() {
		return this.parsedPayload.toString();
	}

	public static JsonMessageWrapper fromString(String rawPayload) {
		return new JsonMessageWrapper(rawPayload);
	}

	public boolean correctStructure() {
		try {
			this.headers();
			this.body();
			this.username();
			this.chatId();
			this.sessionId();
			String type = this.type();
			this.date();
			this.token();

			JsonElement content = this.body().get("content");
			boolean isMediaMessage = MEDIA_MESSAGES.stream()
			                                       .map(Message::type)
			                                       .anyMatch(t -> t.equals(type));
			if (isMediaMessage) {
				content.getAsJsonObject(); // A JSON Object
			} else {
				content.getAsString(); // A JSON String
			}

			return true;
		} catch (Exception e) {
			log.error("Error while checking message structure, ({})", e.getMessage());
			return false;
		}
	}

	/**
	 * Creates a new builder to build a new message.
	 *
	 * @return the builder.
	 */
	public static BuilderImpl builder() {
		return new BuilderImpl();
	}

	/**
	 * Builder implementation for the message wrapper.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class BuilderImpl implements Builder {
		private final JsonObject headers = new JsonObject();
		private final JsonObject body = new JsonObject();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object build() {
			JsonObject result = new JsonObject();
			result.add("headers", this.headers);
			result.add("body", this.body);
			return result;
		}

		/**
		 * Sets the username of the message.
		 *
		 * @param value the username.
		 *
		 * @return the builder.
		 */
		public BuilderImpl username(String value) {
			this.headers.addProperty("username", value);
			return this;
		}

		/**
		 * Sets the chat id of the message.
		 *
		 * @param value the chat id.
		 *
		 * @return the builder.
		 */
		public BuilderImpl chatId(String value) {
			this.headers.addProperty("chatId", value);
			return this;
		}

		/**
		 * Sets the session id of the message.
		 *
		 * @param value the session id.
		 *
		 * @return the builder.
		 */
		public BuilderImpl sessionId(long value) {
			this.headers.addProperty("sessionId", value);
			return this;
		}

		/**
		 * Sets the type of the message.
		 *
		 * @param value the type.
		 *
		 * @return the builder.
		 */
		public BuilderImpl type(String value) {
			this.headers.addProperty("type", value);
			return this;
		}

		/**
		 * Sets the date of the message.
		 *
		 * @param value the date.
		 *
		 * @return the builder.
		 */
		public BuilderImpl date(long value) {
			this.headers.addProperty("date", value);
			return this;
		}

		/**
		 * Sets the token of the message.
		 *
		 * @param value the token.
		 *
		 * @return the builder.
		 */
		public BuilderImpl token(String value) {
			this.headers.addProperty("token", value);
			return this;
		}

		/**
		 * Sets the content of the message.
		 *
		 * @param value the content.
		 *
		 * @return the builder.
		 */
		public BuilderImpl content(String value) {
			this.body.addProperty("content", value);
			return this;
		}
	}
}
