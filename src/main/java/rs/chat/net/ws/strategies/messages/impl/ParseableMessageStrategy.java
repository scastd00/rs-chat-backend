package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.CommandFailureException;
import rs.chat.exceptions.CommandUnavailableException;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.commands.Command;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandMappings;
import rs.chat.net.ws.strategies.commands.parser.MessageParser;
import rs.chat.net.ws.strategies.commands.parser.ParsedData;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.events.CommandMessageEvent;
import rs.chat.net.ws.strategies.messages.events.MentionMessageEvent;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.net.ws.Message.MENTION_MESSAGE;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#PARSEABLE_MESSAGE} messages.
 */
@Slf4j
public class ParseableMessageStrategy extends GenericMessageStrategy {
	private final ApplicationEventPublisher eventPublisher;

	public ParseableMessageStrategy(ChatManagement chatManagement, ApplicationEventPublisher eventPublisher) {
		super(chatManagement);
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		try {
			// This reference allows to send the message content to other clients only once
			AtomicBoolean messageContentSent = new AtomicBoolean(false);

			MessageParser.parse(handlingDTO.wrappedMessage().content())
			             .stream()
			             .filter(Predicate.not(ParsedData::isText)) // Ignore messages
			             .forEach(parsedData -> {
				             switch (parsedData.type()) {
					             case COMMAND -> runCommand(handlingDTO, parsedData, messageContentSent);
					             case MENTION -> sendMentionToUser(handlingDTO, parsedData, messageContentSent);
					             default -> {/**/}
				             }
			             });
		} catch (CommandUnavailableException | IllegalArgumentException e) {
			log.error("Error while parsing message", e);
			handlingDTO.getSession().sendMessage(new TextMessage(
					Utils.createMessage(
							"An error occurred while parsing the message, %s".formatted(e.getMessage()),
							ERROR_MESSAGE.type(),
							"NONE"
					)
			));
		}
	}

	/**
	 * Sends a mention message to the user to notify them.
	 *
	 * @param handlingDTO        DTO containing the required data.
	 * @param parsedData         parsed data from the message (contains the mentioned username).
	 * @param messageContentSent boolean to check if the message has already been sent.
	 */
	private void sendMentionToUser(MessageHandlingDTO handlingDTO, ParsedData parsedData, AtomicBoolean messageContentSent) {
		// If the user is the same as the one who sent the message, ignore it
		if (handlingDTO.wrappedMessage().username().equals(parsedData.data())) {
			return;
		}

		try {
			// Send the message to other clients only if it hasn't been sent yet
			if (!messageContentSent.getAndSet(true)) {
				super.handle(handlingDTO);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String chatId = handlingDTO.wrappedMessage().chatId();
		this.chatManagement.mentionUser(
				chatId,
				parsedData.data(),
				createMessage(
						"You have been mentioned by " + handlingDTO.wrappedMessage().username(),
						MENTION_MESSAGE.type(),
						chatId
				)
		);

		MentionMessageEvent event = new MentionMessageEvent(this, handlingDTO.getClientID().username());
		event.setCallback(badgeCallback(handlingDTO));

		this.eventPublisher.publishEvent(event);
	}

	/**
	 * Runs the command.
	 *
	 * @param handlingDTO        DTO containing the required data.
	 * @param parsedData         parsed data from the message (contains the command).
	 * @param messageContentSent boolean to check if the message has already been sent.
	 */
	private void runCommand(MessageHandlingDTO handlingDTO, ParsedData parsedData, AtomicBoolean messageContentSent) {
		Command command = CommandMappings.getCommand(parsedData.data());
		handlingDTO.otherData().put("commandParams", parsedData.params());

		try {
			// If the command message could be sent to others, send it.
			// If it is for the user only, don't send it.
			// The message is only sent once, so if it has already been sent, don't send it again.
			if (command.sendToOthers() && !messageContentSent.getAndSet(true)) {
				super.handle(handlingDTO);
			}

			command.strategy().handle(new CommandHandlingDTO(this.chatManagement, handlingDTO.otherData()));

			CommandMessageEvent event = new CommandMessageEvent(this, handlingDTO.getClientID().username());
			event.setCallback(badgeCallback(handlingDTO));
			event.setCommand(command.command());

			this.eventPublisher.publishEvent(event);
		} catch (IOException e) {
			throw new CommandFailureException(e.getMessage());
		}
	}
}
