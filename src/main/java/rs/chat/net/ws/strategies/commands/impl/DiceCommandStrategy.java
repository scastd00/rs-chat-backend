package rs.chat.net.ws.strategies.commands.impl;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandStrategy;

import java.io.IOException;
import java.util.Optional;

import static rs.chat.net.ws.Message.COMMAND_RESPONSE;
import static rs.chat.Constants.SECURE_RANDOM;
import static rs.chat.net.ws.JsonMessageWrapper.createMessage;

@Slf4j
public class DiceCommandStrategy implements CommandStrategy {
	@Override
	public void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException {
		ClientID clientID = handlingDTO.getClientID();
		String messageContent;
		String userToChallenge = Optional.ofNullable(handlingDTO.getParams())
		                                 .map(params -> params.get("user"))
		                                 .orElse("");

		if (!userToChallenge.equals("")) {
			messageContent = String.format("@%s has challenged @%s to a dice game!", clientID.username(), userToChallenge);
		} else {
			messageContent = String.format("@%s has rolled a dice and got %d!", clientID.username(), this.rollDice());
		}

		handlingDTO.chatManagement().broadcastToSingleChatAndSave(
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
