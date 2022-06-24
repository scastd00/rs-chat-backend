package ule.chat.net;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseBody {
	private final Map<String, Object> data;

	public HttpResponseBody() {
		this.data = new HashMap<>();
	}

	public HttpResponseBody(String key, Object value) {
		this.data = Map.of(key, value);
	}

	public Map<String, Object> getData() {
		return this.data;
	}

	public HttpResponseBody addSingle(String key, Object value) {
		this.data.put(key, value);
		return this;
	}

	public HttpResponseBody addObject(String key, Map<String, Object> value) {
		this.data.put(key, value);
		return this;
	}
}
