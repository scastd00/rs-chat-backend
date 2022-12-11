package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.CommandUnavailableException;
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
import java.util.function.Predicate;

/**
 * Strategy for handling {@link Message#PARSEABLE_MESSAGE} messages.
 */
@Slf4j
public class ParseableMessageStrategy extends GenericMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		super.handle(wrappedMessage, chatManagement, otherData); // Send the message to other clients

		try {
			MessageParser.parse(wrappedMessage.content())
			             .stream()
			             .filter(Predicate.not(ParsedData::isMessage)) // Ignore messages
			             .forEach(parsedData -> {
				             switch (parsedData.type()) {
					             case COMMAND -> {
						             Command command = CommandMappings.getCommand(parsedData.data());
						             otherData.put("commandParams", parsedData.params());

						             try {
							             command.strategy().handle(chatManagement, otherData);
						             } catch (IOException e) {
							             throw new RuntimeException(e);
						             }
					             }
					             case MENTION -> log.info("Mention"); // Todo: implement. Send special message to notify the user with a sound.
					             default -> log.info("Message");
				             }
			             });
		} catch (CommandUnavailableException | IllegalArgumentException e) {
			log.error("Error while parsing message", e);
			// Todo: Warn the user that an error occurred
		}
	}
}
