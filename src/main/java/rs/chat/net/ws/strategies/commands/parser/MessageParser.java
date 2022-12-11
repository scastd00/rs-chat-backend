package rs.chat.net.ws.strategies.commands.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.strategies.commands.Command;
import rs.chat.net.ws.strategies.commands.CommandMappings;
import rs.chat.net.ws.strategies.commands.CommandParams;

import java.util.ArrayList;
import java.util.List;

import static rs.chat.net.ws.strategies.commands.parser.ParsedData.Type.COMMAND;
import static rs.chat.net.ws.strategies.commands.parser.ParsedData.Type.MENTION;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageParser {
	private static final String COMMAND_PREFIX = "/";
	private static final String MENTION_PREFIX = "@";

	public static List<ParsedData> parse(String fullMessage) {
		String[] messageParts = fullMessage.split(" ");
		List<ParsedData> datas = new ArrayList<>();

		for (int i = 0; i < messageParts.length; i++) {
			ParsedData parsedData = switch (messageParts[i].substring(0, 1)) {
				case COMMAND_PREFIX -> parseCommand(messageParts, i);
				case MENTION_PREFIX -> parseMention(messageParts, i);
				default -> parseMessage(messageParts, i);
			};

			datas.add(parsedData);
		}

		return datas;
	}

	private static ParsedData parseCommand(String[] parts, int pos) {
		Command command = CommandMappings.getCommand(parts[pos]);

		int parameters = command.paramNames().length;

		if (parameters == 0) {
			return new ParsedData(parts[pos], null, COMMAND);
		}

		if (pos + parameters >= parts.length) {
			throw new IllegalArgumentException("Not enough parameters for command " + parts[pos]);
		}

		CommandParams commandParams = new CommandParams(command.paramNames());
		for (int i = 0; i < parameters; i++) {
			// Todo: check that the parameter is valid.
			commandParams.put(command.paramNames()[i], parts[pos + i + 1]);
		}

		return new ParsedData(parts[pos], commandParams, COMMAND);
	}

	private static ParsedData parseMention(String[] parts, int pos) {
		return new ParsedData(parts[pos], null, MENTION);
	}

	private static ParsedData parseMessage(String[] parts, int pos) {
		return new ParsedData(parts[pos], null, ParsedData.Type.MESSAGE);
	}
}
