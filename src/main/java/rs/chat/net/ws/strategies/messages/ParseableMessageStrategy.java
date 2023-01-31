package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.CommandFailureException;
import rs.chat.exceptions.CommandUnavailableException;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.commands.Command;
import rs.chat.net.ws.strategies.commands.CommandMappings;
import rs.chat.net.ws.strategies.commands.parser.MessageParser;
import rs.chat.net.ws.strategies.commands.parser.ParsedData;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.net.ws.Message.MENTION_MESSAGE;
import static rs.chat.utils.Utils.createMessage;

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
					             case COMMAND -> runCommand(chatManagement, otherData, parsedData);
					             case MENTION -> sendMentionToUser(wrappedMessage, chatManagement, parsedData);
					             default -> {/**/}
				             }
			             });
		} catch (CommandUnavailableException | IllegalArgumentException e) {
			log.error("Error while parsing message", e);
			String errorMessage = Utils.createMessage(
					"An error occurred while parsing the message, %s".formatted(e.getMessage()),
					ERROR_MESSAGE.type(),
					"NONE"
			);
			getSession(otherData).sendMessage(new TextMessage(errorMessage));
		}
	}

	/**
	 * Sends a mention message to the user to notify them.
	 *
	 * @param wrappedMessage message received from the client.
	 * @param chatManagement chat management to send the message.
	 * @param parsedData     parsed data from the message (contains the mentioned username).
	 */
	private static void sendMentionToUser(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement, ParsedData parsedData) {
		// If the user is the same as the one who sent the message, ignore it
		if (wrappedMessage.username().equals(parsedData.data())) {
			return;
		}

		String chatId = wrappedMessage.chatId();
		chatManagement.mentionUser(
				chatId,
				parsedData.data(),
				createMessage(
						"You have been mentioned by " + wrappedMessage.username(),
						MENTION_MESSAGE.type(),
						chatId
				)
		);
	}

	/**
	 * Runs the command.
	 *
	 * @param chatManagement chat management to send the message.
	 * @param otherData      other data to pass to the command.
	 * @param parsedData     parsed data from the message (contains the command).
	 */
	private static void runCommand(ChatManagement chatManagement, Map<String, Object> otherData,
	                               ParsedData parsedData) {
		Command command = CommandMappings.getCommand(parsedData.data());
		otherData.put("commandParams", parsedData.params());

		try {
			command.strategy().handle(chatManagement, otherData);
		} catch (IOException e) {
			throw new CommandFailureException(e.getMessage());
		}
	}
}
