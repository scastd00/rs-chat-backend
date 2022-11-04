package rs.chat.net.ws.strategies;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link Message#MAINTENANCE_MESSAGE} messages.
 */
@Slf4j
public class MaintenanceMessageStrategy extends GenericMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage);
		webSocketChatMap.totalBroadcast(wrappedMessage.toString());
	}
}
