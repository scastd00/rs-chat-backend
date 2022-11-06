package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.net.ws.strategies.commands.CommandStrategy;
import rs.chat.net.ws.strategies.commands.StrategyMappings;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link Message#COMMAND_MESSAGE} messages.
 */
@Slf4j
public class CommandMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		String[] commandParts = wrappedMessage.content().split(" ");

		if (commandParts.length == 2) {
			otherData.put("commandText", commandParts[1]);
		}

		CommandStrategy strategy = StrategyMappings.decideStrategy(commandParts[0]);

		try {
			log.debug("Executing command: {}", commandParts[0]);
			strategy.handle(webSocketChatMap, otherData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
