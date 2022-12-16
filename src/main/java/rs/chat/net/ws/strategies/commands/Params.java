package rs.chat.net.ws.strategies.commands;

import java.util.HashMap;
import java.util.Map;

public class Params {
	private final Map<String, String> paramsMap = new HashMap<>();

	public Params(String... paramNames) {
		for (String paramName : paramNames) {
			this.paramsMap.put(paramName, null);
		}
	}

	public String get(String key) {
		return this.paramsMap.get(key);
	}

	public void put(String key, String value) {
		this.paramsMap.put(key, value);
	}
}
