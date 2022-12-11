package rs.chat.net.ws.strategies.commands.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.CommandUnavailableException;
import rs.chat.net.ws.strategies.commands.Command;
import rs.chat.net.ws.strategies.commands.CommandMappings;

import java.util.ArrayList;
import java.util.List;

import static rs.chat.utils.Constants.STRING_ARRAY;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageParser {
	private static final String COMMAND_PREFIX = "/";
	private static final String MENTION_PREFIX = "@";

	public static List<ParsedData> parse(String fullMessage) {
		String[] messageParts = fullMessage.split(" ");
		List<String> params = new ArrayList<>();
		List<ParsedData> datas = new ArrayList<>();
		int parameters;
		Command command;

		for (int i = 0; i < messageParts.length; i++) {
			String messagePart = messageParts[i];

			if (!messagePart.startsWith(COMMAND_PREFIX)) {
				datas.add(new ParsedData(messagePart, null, false)); // Not a command, just a message.
				continue;
			}

			try {
				command = CommandMappings.getCommand(messagePart);
			} catch (CommandUnavailableException e) {
				log.warn("Command not found: {}", messagePart);
				datas.add(new ParsedData(messagePart, null, false)); // Incorrect command, but we send the message.
				continue;
			}

			parameters = command.params();
			if (parameters == 0) {
				datas.add(new ParsedData(messagePart, null, true));
				continue;
			}

			for (int j = 0; j < parameters; j++) {
				if (i + j + 1 >= messageParts.length) {
					log.warn("Not enough parameters for command {}.", messagePart);
					break;
				}

				params.add(messageParts[i + j + 1]);
			}

			datas.add(new ParsedData(messagePart, List.of(params.toArray(STRING_ARRAY)), true));
			params.clear();
			i += parameters; // Skip the parameters.
		}

		return datas;
	}
}
