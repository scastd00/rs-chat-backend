package rs.chat.net.ws;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.utils.Builder;
import rs.chat.utils.Utils;

/**
 * Class that wraps the received JSON messages. They follow the next structure:
 * <pre>{@code
 * {
 * 	"headers": {
 * 	   "username": "<username>",
 * 	   "chatId": "<chatId>",
 * 	   "sessionId": "<sessionId>",
 * 	   "type": "<typeConstant>",
 * 	   "date": "<currentDate>",
 * 	   "token": "Bearer <token>"
 *  },
 * 	"body": {
 * 	   "content": "<contentOfMessage>"
 *  }
 * }
 * }</pre>
 */
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
		this.parsedPayload = Utils.parseJson(rawPayload);
	}

	/**
	 * @return the raw payload of the message.
	 */
	public String getRawPayload() {
		return this.rawPayload;
	}

	/**
	 * @return the parsed payload of the message.
	 */
	public JsonObject getParsedPayload() {
		return this.parsedPayload;
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
		return this.headers().get("chatId").getAsString(); // Fixme: Maybe get as Long and change related problems??
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
