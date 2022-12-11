package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.commands.Command;
import rs.chat.net.ws.strategies.commands.CommandMappings;
import rs.chat.net.ws.strategies.commands.parser.MessageParser;
import rs.chat.net.ws.strategies.commands.parser.ParsedData;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link Message#PARSEABLE_MESSAGE} messages.
 */
@Slf4j
public class ParseableMessageStrategy extends GenericMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		super.handle(wrappedMessage, chatManagement, otherData); // Send the message to other clients

		MessageParser.parse(wrappedMessage.content())
		             .stream()
		             .filter(ParsedData::isCommand) // Filter only commands
		             .map(ParsedData::data)
		             .map(CommandMappings::getCommand)
		             .map(Command::strategy)
		             .forEach(strategy -> {
			             try {
				             strategy.handle(chatManagement, otherData);
			             } catch (WebSocketException | IOException e) {
				             log.error("Error while handling command", e);
			             }
		             });
	}
}
