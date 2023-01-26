package rs.chat.net.ws.strategies.messages;

import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.cache.CachedHistoryFile;
import rs.chat.cache.HistoryFilesCache;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.GET_HISTORY_MESSAGE;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#GET_HISTORY_MESSAGE} messages.
 */
@Slf4j
public class GetHistoryStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		/*
		 * Messages are read from the history file and sent to the client. This file is cached
		 * in memory to speed up the process of reading the messages. The cache is updated
		 * every time a new message is sent to the chat. The client must send the page number
		 * of the history file that it wants to read. The default page size is {@link Constants#HISTORY_PAGE_SIZE}.
		 */

		// The offset of the messages that the client has already received
		int numberOfReceivedMessagesByClient = Integer.parseInt(wrappedMessage.content());
		CachedHistoryFile historyFile = HistoryFilesCache.INSTANCE.get(wrappedMessage.chatId());

		JsonArray lastMessages = historyFile.getMoreMessagesFromOffset(numberOfReceivedMessagesByClient)
		                                    .stream()
		                                    .map(Utils::parseJson)
		                                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);

		getSession(otherData).sendMessage(new TextMessage(
				createMessage(lastMessages.toString(), GET_HISTORY_MESSAGE.type(), wrappedMessage.chatId())
		));
	}
}
