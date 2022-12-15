package rs.chat.net.ws.strategies.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandParams {
	private final Map<String, String> params = new HashMap<>();

	public CommandParams(String... paramNames) {
		for (String paramName : paramNames) {
			this.params.put(paramName, null);
		}
	}

	public String get(String key) {
		return this.params.get(key);
	}

	public void put(String key, String value) {
		this.params.put(key, value);
	}
}
