package rs.chat.net.ws.strategies.commands.parser;

import org.jetbrains.annotations.NotNull;
import rs.chat.net.ws.strategies.commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents a map of parameters.
 */
public class Params {
	public static final Params NONE = Params.forCommand(Command.$NOOP);
	private final Map<String, String> paramsMap = new HashMap<>();

	/**
	 * Creates a new {@link Params} object for the given names and empty values.
	 *
	 * @param paramNames names of the parameters.
	 */
	private Params(String... paramNames) {
		for (String paramName : paramNames) {
			this.paramsMap.put(paramName, "");
		}
	}

	/**
	 * Creates a new {@link Params} object with all the parameter names of the given command.
	 *
	 * @param command command to get the parameters from.
	 *
	 * @return a new {@link Params} object.
	 */
	public static Params forCommand(Command command) {
		return new Params(command.paramNames());
	}

	/**
	 * Gets the value of the parameter with the given key.
	 *
	 * @param key key of the parameter.
	 *
	 * @return value of the parameter.
	 */
	@NotNull
	public String get(String key) {
		return this.paramsMap.get(key);
	}

	/**
	 * Adds a new parameter to the map with the given key and value.
	 *
	 * @param key   key of the parameter.
	 * @param value value of the parameter.
	 */
	public void put(@NotNull String key, @NotNull String value) {
		this.paramsMap.put(key, value);
	}
}
