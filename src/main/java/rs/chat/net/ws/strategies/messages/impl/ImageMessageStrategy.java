package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.events.ImageMessageEvent;

import java.io.IOException;

/**
 * Strategy for handling {@link Message#IMAGE_MESSAGE} messages.
 */
@Slf4j
public class ImageMessageStrategy extends GenericMessageStrategy {
	private final ApplicationEventPublisher eventPublisher;

	public ImageMessageStrategy(ChatManagement chatManagement, ApplicationEventPublisher eventPublisher) {
		super(chatManagement);
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		super.handle(handlingDTO);

		ImageMessageEvent event = new ImageMessageEvent(this, handlingDTO.getClientID().username());
		event.setCallback(badgeCallback(handlingDTO));

		this.eventPublisher.publishEvent(event);
	}
}
