package rs.chat.net;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class HttpResponseBody {
	private final Map<String, Object> data = new HashMap<>();
	public static final HttpResponseBody EMPTY = new HttpResponseBody("data", "");

	public HttpResponseBody(String key, Object value) {
		this.addSingle(key, value);
	}

	public HttpResponseBody addSingle(String key, Object value) {
		this.data.put(key, value);
		return this;
	}

	public HttpResponseBody addObject(String key, Map<String, ?> value) {
		this.data.put(key, value);
		return this;
	}
}
