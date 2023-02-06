package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.events.PdfMessageEvent;

import java.io.IOException;

/**
 * Strategy for handling {@link Message#PDF_MESSAGE} messages.
 */
@Slf4j
public class PdfMessageStrategy extends GenericMessageStrategy {
	private final ApplicationEventPublisher eventPublisher;

	public PdfMessageStrategy(ChatManagement chatManagement, ApplicationEventPublisher eventPublisher) {
		super(chatManagement);
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		super.handle(handlingDTO);
		this.eventPublisher.publishEvent(new PdfMessageEvent(this, handlingDTO.getClientID().username()));
	}
}
