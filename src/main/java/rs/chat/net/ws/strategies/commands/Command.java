package rs.chat.net.ws.strategies.commands;

/**
 * Record that represents a command that can be executed by a client.
 *
 * @param command     The command to be executed.
 * @param type        The type of command.
 * @param description A description of the command.
 * @param usage       The usage of the command.
 * @param strategy    The strategy to be used to execute the command.
 */
public record Command(String command, CommandType type, String description, String usage, CommandStrategy strategy) {
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
	enum CommandType {
		NORMAL,
		TEACHER,
		ADMIN
	}
}
