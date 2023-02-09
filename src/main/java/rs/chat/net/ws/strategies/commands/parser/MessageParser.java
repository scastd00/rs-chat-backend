package rs.chat.net.ws.strategies.commands.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.strategies.commands.Command;
import rs.chat.net.ws.strategies.commands.CommandMappings;

import java.util.ArrayList;
import java.util.List;

import static rs.chat.net.ws.strategies.commands.parser.ParsedData.Type.COMMAND;
import static rs.chat.net.ws.strategies.commands.parser.ParsedData.Type.MENTION;
import static rs.chat.net.ws.strategies.commands.parser.ParsedData.Type.MESSAGE;

/**
 * Parser of messages containing special actions to perform.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageParser {
	private static final String COMMAND_PREFIX = "/";
	private static final String MENTION_PREFIX = "@";

	/**
	 * Parses a message and returns a {@link List} of {@link ParsedData}.
	 *
	 * @param fullMessage message to parse.
	 *
	 * @return a {@link List} of {@link ParsedData}.
	 */
	public static List<ParsedData> parse(String fullMessage) {
		String[] messageParts = fullMessage.split(" ");
		List<ParsedData> datas = new ArrayList<>();

		for (int i = 0; i < messageParts.length; i++) {
			ParsedData parsedData = switch (messageParts[i].substring(0, 1)) {
				case COMMAND_PREFIX -> parseCommand(messageParts, i);
				case MENTION_PREFIX -> parseMention(messageParts[i]);
				default -> parseMessage(messageParts[i]);
			};

			datas.add(parsedData);
		}

		return datas;
	}

	/**
	 * Parses a command. If it is made of multiple words, it will be parsed as a single command with params.
	 * <p>
	 * The following parameters are needed in order to be able to parse the options of the command (in
	 * case they are needed).
	 *
	 * @param parts message parts.
	 * @param pos   position of the command.
	 *
	 * @return a {@link ParsedData} containing the command and its params.
	 */
	private static ParsedData parseCommand(String[] parts, int pos) {
		Command command = CommandMappings.getCommand(parts[pos]);
		int parameters = command.paramNames().length;

		// Command without parameters
		if (parts.length - 1 == 0) {
			return new ParsedData(parts[pos], null, COMMAND);
		}

		// Check if the command has more parameters than needed
		if (pos + parameters >= parts.length) {
			throw new IllegalArgumentException("Wrong number of parameters for command " + parts[pos]);
		}

		// Save the parameters
		Params params = new Params(command.paramNames());
		for (int i = 0; i < parameters; i++) {
			// Todo: check that the parameter is valid.
			String paramName = command.paramNames()[i];
			String paramValue = parts[pos + i + 1];

			// Users can be mentioned by their username with the @ prefix, so we remove it if it is present.
			if (paramName.equals("user") && paramValue.startsWith(MENTION_PREFIX)) {
				paramValue = paramValue.substring(1);
			}

			params.put(paramName, paramValue);
		}

		return new ParsedData(parts[pos], params, COMMAND);
	}

	/**
	 * Parses a mention. Mentions are made of a single word, and are prefixed with the @ symbol and may
	 * be followed by symbols.
	 *
	 * @param mention mention to parse.
	 *
	 * @return a {@link ParsedData} containing the mention.
	 */
	private static ParsedData parseMention(String mention) {
		return new ParsedData(mention.split("\\W+")[1], null, MENTION);
	}

	/**
	 * Parses a message. Messages are made of a single word, and are not prefixed with any symbol.
	 * This is the default case and is used to determine the parts to ignore when sending special
	 * messages.
	 *
	 * @param part message part to parse.
	 *
	 * @return a {@link ParsedData} containing the message.
	 */
	private static ParsedData parseMessage(String part) {
		return new ParsedData(part, null, MESSAGE);
	}
}
