package rs.chat.net.ws.strategies.commands.impl;

import lombok.extern.slf4j.Slf4j;
import rs.chat.ai.eightball.EightBall;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.strategies.commands.CommandHandlingDTO;
import rs.chat.net.ws.strategies.commands.CommandStrategy;
import rs.chat.net.ws.strategies.commands.parser.Params;

import java.io.IOException;

import static rs.chat.net.ws.JsonMessageWrapper.createMessage;
import static rs.chat.net.ws.Message.COMMAND_RESPONSE;

@Slf4j
public class EightBallCommandStrategy implements CommandStrategy {
	@Override
	public void handle(CommandHandlingDTO handlingDTO) throws WebSocketException, IOException {
		// Send a random answer to the user
		ClientID clientID = handlingDTO.getClientID();
		String chatId = clientID.chatId();
		Params params = handlingDTO.getParams();
		String question = params.get("question");

		String reply = EightBall.getReply(question);
		handlingDTO.chatManagement()
		           .broadcastToSingleChatWithoutSaving(
				           chatId,
				           createMessage(
						           reply,
						           COMMAND_RESPONSE.type(),
						           chatId
				           )
		           );
	}
}
