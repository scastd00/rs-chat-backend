package rs.chat.net.ws.strategies.commands;

import java.util.Arrays;
import java.util.Objects;

/**
 * Record that represents a command that can be executed by a client.
 *
 * @param command     The command to be executed.
 * @param type        The type of command.
 * @param description A description of the command.
 * @param usage       The usage of the command.
 * @param strategy    The strategy to be used to execute the command.
 * @param paramNames  The names of the parameters that the command takes.
 */
public record Command(
		String command,
		CommandType type,
		String description,
		String usage,
		CommandStrategy strategy,
		String... paramNames) {
	/**
	 * Type of the command.
	 * <p>
	 * The type of the command determines how the command is executed.
	 * <ul>
	 *     <li>{@link CommandType#NORMAL} - The command is executed by all clients.</li>
	 *     <li>{@link CommandType#TEACHER} - The command is executed by the client that sent the command if the client is a teacher.</li>
	 *     <li>{@link CommandType#ADMIN} - The command is executed by the client that sent the command if the client is an admin.</li>
	 * </ul>
	 */
	public enum CommandType {
		NORMAL,
		TEACHER,
		ADMIN
	}

	@Override
	public String toString() {
		return "Command{" +
				"command='/" + command + '\'' +
				", type=" + type +
				", description='" + description + '\'' +
				", usage='" + usage + '\'' +
				", strategy=" + strategy +
				", paramNames=" + Arrays.toString(paramNames) +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Command command1 = (Command) o;
		return Objects.equals(command, command1.command) &&
				type == command1.type &&
				Objects.equals(description, command1.description) &&
				Objects.equals(usage, command1.usage) &&
				Objects.equals(strategy, command1.strategy) &&
				Arrays.equals(paramNames, command1.paramNames);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(command, type, description, usage, strategy);
		result = 31 * result + Arrays.hashCode(paramNames);
		return result;
	}
}
