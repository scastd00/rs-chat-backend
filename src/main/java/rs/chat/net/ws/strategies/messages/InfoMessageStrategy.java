package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link Message#INFO_MESSAGE} messages.
 */
@Slf4j
public class InfoMessageStrategy extends GenericMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage);
		chatManagement.totalBroadcast(wrappedMessage.toString());
	}
}
