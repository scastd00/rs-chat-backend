package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.events.VideoMessageEvent;

import java.io.IOException;

/**
 * Strategy for handling {@link Message#VIDEO_MESSAGE} messages.
 */
@Slf4j
public class VideoMessageStrategy extends GenericMessageStrategy {
	private final ApplicationEventPublisher eventPublisher;

	public VideoMessageStrategy(ChatManagement chatManagement, ApplicationEventPublisher eventPublisher) {
		super(chatManagement);
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		super.handle(handlingDTO);

		VideoMessageEvent event = new VideoMessageEvent(this, handlingDTO.getClientID().username());
		event.setCallback(badgeCallback(handlingDTO));

		this.eventPublisher.publishEvent(event);
	}
}
