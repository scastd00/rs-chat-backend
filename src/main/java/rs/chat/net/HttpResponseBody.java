package rs.chat.net;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class HttpResponseBody {
	private final Map<String, Object> data = new HashMap<>();
	public static final HttpResponseBody EMPTY = null;

	public HttpResponseBody(String key, Object value) {
		this.add(key, value);
	}

	public HttpResponseBody add(String key, Object value) {
		this.data.put(key, value);
		return this;
	}
}
