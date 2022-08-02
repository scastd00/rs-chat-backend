package rs.chat.net.ws;

import com.google.gson.JsonObject;
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
 * 	   "token": "Bearer <accessToken>"
 *  },
 * 	"body": {
 * 	   "encoding": "regex(UTF-8|base64)",
 * 	   "content": "<encodedContentOfMessage>"
 *  }
 * }
 * }</pre>
 */
public class JsonMessageWrapper {
	private final String rawPayload;
	private final JsonObject parsedPayload;

	public JsonMessageWrapper(String rawPayload) {
		this.rawPayload = rawPayload;
		this.parsedPayload = Utils.parseJson(rawPayload);
	}

	public String getRawPayload() {
		return this.rawPayload;
	}

	public JsonObject getParsedPayload() {
		return this.parsedPayload;
	}

	public JsonObject headers() {
		return (JsonObject) this.parsedPayload.get("headers");
	}

	public JsonObject body() {
		return (JsonObject) this.parsedPayload.get("body");
	}

	public String username() {
		return this.headers().get("username").getAsString();
	}

	public String chatId() {
		return this.headers().get("chatId").getAsString();
	}

	public long sessionId() {
		return this.headers().get("sessionId").getAsLong();
	}

	public String type() {
		return this.headers().get("type").getAsString();
	}

	public long date() {
		return this.headers().get("date").getAsLong();
	}

	public String token() {
		return this.headers().get("token").getAsString();
	}

	public String encoding() {
		return this.body().get("encoding").getAsString();
	}

	public String content() {
		return this.body().get("content").getAsString();
	}

	public static BuilderImpl builder() {
		return new BuilderImpl();
	}

	public static class BuilderImpl implements Builder {
		private final JsonObject headers = new JsonObject();
		private final JsonObject body = new JsonObject();

		private BuilderImpl() {
		}

		@Override
		public Object build() {
			JsonObject result = new JsonObject();
			result.add("headers", this.headers);
			result.add("body", this.body);
			return result;
		}

		public BuilderImpl username(String value) {
			this.headers.addProperty("username", value);
			return this;
		}

		public BuilderImpl chatId(String value) {
			this.headers.addProperty("chatId", value);
			return this;
		}

		public BuilderImpl sessionId(long value) {
			this.headers.addProperty("sessionId", value);
			return this;
		}

		public BuilderImpl type(String value) {
			this.headers.addProperty("type", value);
			return this;
		}

		public BuilderImpl date(long value) {
			this.headers.addProperty("date", value);
			return this;
		}

		public BuilderImpl token(String value) {
			this.headers.addProperty("token", value);
			return this;
		}

		public BuilderImpl encoding(String value) {
			this.body.addProperty("encoding", value);
			return this;
		}

		public BuilderImpl content(String value) {
			this.body.addProperty("content", value);
			return this;
		}
	}
}
