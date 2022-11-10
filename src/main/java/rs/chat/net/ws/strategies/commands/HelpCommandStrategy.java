package rs.chat.net.ws.strategies.commands;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.utils.Utils.createMessage;

public class HelpCommandStrategy implements CommandStrategy {
	@Override
	public void handle(ChatManagement chatManagement, Map<String, Object> otherData)
			throws WebSocketException, IOException {
		getSession(otherData).sendMessage(new TextMessage(
				createMessage(
						"Available commands: " + StrategyMappings.getAvailableCommandsWithDescriptionAndUsage(),
						COMMAND_RESPONSE.type(),
						getClientID(otherData).chatId()
				)
		));
	}

	@Override
	public String getDescriptionOfCommand() {
		return "Displays all available commands or information about a specific command.";
	}

	@Override
	public String getUsageOfCommand() {
		return "/help | /help <command>"; // Todo: implement specific command help
	}
}
