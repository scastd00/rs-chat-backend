package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.commands.Command;
import rs.chat.net.ws.strategies.commands.StrategyMappings;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link Message#COMMAND_MESSAGE} messages.
 */
@Slf4j
public class CommandMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		String[] commandParts = wrappedMessage.content().split(" ");

		if (commandParts.length == 2) {
			otherData.put("commandParams", commandParts[1]);
		}

		Command command = StrategyMappings.getCommand(commandParts[0]);

		try {
			log.debug("Executing command: {}", commandParts[0]);
			command.strategy().handle(chatManagement, otherData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
