package rs.chat.net.ws.strategies.commands;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.exceptions.CommandUnavailableException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static rs.chat.net.ws.strategies.commands.Command.$NOOP;
import static rs.chat.net.ws.strategies.commands.Command.ALL_COMMANDS;

/**
 * Class that maps commands to their respective strategies.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1192") // Suppress "String literals should not be duplicated" warning
public final class CommandMappings {
	private static final Map<String, Command> commands = new HashMap<>();

	static {
		ALL_COMMANDS.forEach(command -> commands.put(command.command(), command));
	}

	/**
	 * Returns the {@link Command} with the specified name. If no command is found,
	 * then {@link Command#$NOOP} is returned.
	 *
	 * @param commandStr The name of the command.
	 *
	 * @return The {@link Command} with the specified name, or {@link Command#$NOOP} if no
	 * command is found.
	 */
	public static Command getCommand(String commandStr) {
		return commands.getOrDefault(commandStr, $NOOP);
	}

	/**
	 * @return A list of all available commands with its usage.
	 */
	public static String getAvailableCommandsWithDescriptionAndUsage() {
		StringBuilder sb = new StringBuilder("##");

		commands.forEach((commandStr, command) -> sb.append(commandStr)
		                                          .append(" - ")
		                                          .append(command.description())
		                                          .append(" Usage: ")
		                                          .append(command.usage())
		                                          .append("##")
		);

		int length = sb.length();
		return sb.replace(length - 2, length, "").toString();
	}
}
