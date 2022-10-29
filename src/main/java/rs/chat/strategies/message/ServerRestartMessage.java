package rs.chat.strategies.message;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.tasks.DefaultTasks;
import rs.chat.tasks.ShutdownServerTask;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link WSMessage#RESTART_MESSAGE} messages.
 */
public class ServerRestartMessage implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		ShutdownServerTask shutdownTask = DefaultTasks.SHUTDOWN;
		shutdownTask.setWebSocketChatMap(webSocketChatMap);

		Utils.executeTask(shutdownTask);
	}
}
