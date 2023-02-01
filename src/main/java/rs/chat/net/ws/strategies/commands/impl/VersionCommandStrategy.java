package rs.chat.net.ws.strategies.commands.impl;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.utils.Constants.CHAT_VERSION;
import static rs.chat.utils.Utils.createMessage;

public class VersionCommandStrategy implements CommandStrategy {
	@Override
	public void handle(ChatManagement chatManagement, Map<String, Object> otherData)
			throws WebSocketException, IOException {
		getSession(otherData).sendMessage(
				new TextMessage(createMessage(
						"Chat version: " + CHAT_VERSION,
						COMMAND_RESPONSE.type(),
						getClientID(otherData).chatId()
				))
		);
	}

}
