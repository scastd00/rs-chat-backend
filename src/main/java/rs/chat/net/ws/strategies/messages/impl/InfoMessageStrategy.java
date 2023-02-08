package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Message;

/**
 * Strategy for handling {@link Message#INFO_MESSAGE} messages.
 */
@Slf4j
public class InfoMessageStrategy extends GenericScheduledMessageStrategy {
	public InfoMessageStrategy(ChatManagement chatManagement) {
		super(chatManagement);
	}
}
