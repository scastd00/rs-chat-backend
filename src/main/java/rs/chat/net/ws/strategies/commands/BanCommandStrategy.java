package rs.chat.net.ws.strategies.commands;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;

import java.io.IOException;
import java.util.Map;

public class BanCommandStrategy implements CommandStrategy {
	@Override
	public void handle(ChatManagement chatManagement, Map<String, Object> otherData)
			throws WebSocketException, IOException {

	}
}
