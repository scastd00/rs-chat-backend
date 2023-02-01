package rs.chat.net.ws.strategies.commands.impl;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;
import java.util.Map;

public class QuitCommandStrategy implements CommandStrategy {
	@Override
	public void handle(ChatManagement chatManagement, Map<String, Object> otherData)
			throws WebSocketException, IOException {

	}
}
