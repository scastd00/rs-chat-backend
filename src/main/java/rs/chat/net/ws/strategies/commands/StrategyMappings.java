package rs.chat.net.ws.strategies.commands;

import rs.chat.exceptions.CommandUnavailableException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StrategyMappings {
	private static final Map<String, CommandStrategy> strategies = new HashMap<>();

	static {
		strategies.put("/time", new TimeCommandStrategy());
	}

	public static CommandStrategy decideStrategy(String command) {
		return Optional.ofNullable(strategies.get(command))
		               .orElseThrow(() -> new CommandUnavailableException("Command " + command + " is not available."));
	}
}
