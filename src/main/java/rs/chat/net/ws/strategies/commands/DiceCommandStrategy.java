package rs.chat.net.ws.strategies.commands;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.strategies.commands.parser.Params;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.utils.Constants.SECURE_RANDOM;
import static rs.chat.utils.Utils.createMessage;

@Slf4j
public class DiceCommandStrategy implements CommandStrategy {
	@Override
	public void handle(ChatManagement chatManagement, Map<String, Object> otherData) throws WebSocketException, IOException {
		ClientID clientID = getClientID(otherData);
		String userToChallenge = Optional.ofNullable((otherData.get("commandParams")))
		                                 .map(params -> ((Params) params).get("user"))
		                                 .orElse(null);
		String messageContent;

		if (userToChallenge != null) {
			messageContent = String.format("@%s has challenged @%s to a dice game!", clientID.username(), userToChallenge);
		} else {
			messageContent = String.format("@%s has rolled a dice and got %d!", clientID.username(), this.rollDice());
		}

		chatManagement.broadcastToSingleChat(
				clientID.chatId(),
				createMessage(
						messageContent,
						COMMAND_RESPONSE.type(),
						clientID.chatId()
				)
		);
	}

	private int rollDice() {
		return SECURE_RANDOM.nextInt(6) + 1;
	}
}
