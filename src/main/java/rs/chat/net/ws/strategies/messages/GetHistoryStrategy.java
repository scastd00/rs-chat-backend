package rs.chat.net.ws.strategies.messages;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.cache.CachedHistoryFile;
import rs.chat.cache.HistoryFilesCache;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.Message.USER_JOINED;
import static rs.chat.net.ws.Message.USER_LEFT;
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

		String username = ((ClientID) otherData.get("clientID")).username();
		int page = Integer.parseInt(wrappedMessage.content());
		CachedHistoryFile historyFile = HistoryFilesCache.INSTANCE.get(wrappedMessage.chatId());

		JsonArray lastMessages = historyFile.getPage(page)
		                                    .stream()
		                                    .map(Utils::parseJson)
		                                    .filter(jsonObject -> this.filterUserActivityMessages(jsonObject, username))
		                                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);

		getSession(otherData).sendMessage(new TextMessage(
				createMessage(lastMessages.toString(), GET_HISTORY_MESSAGE.type(), wrappedMessage.chatId())
		));
	}

	/**
	 * Filter user activity messages of the current user from sending to the client.
	 *
	 * @param jsonObject {@link JsonObject} with all the messages in the history file,
	 *                   it will be modified by removing the activity
	 *                   messages of the current user.
	 * @param username   {@link String} with the current user's username.
	 *
	 * @return {@code true} if the message is not an activity message of the current user,
	 * {@code false} otherwise.
	 */
	private boolean filterUserActivityMessages(JsonObject jsonObject, String username) {
		JsonObject headers = (JsonObject) jsonObject.get("headers");
		String type = headers.get("type").getAsString();

		if (type.equals(USER_JOINED.type()) || type.equals(USER_LEFT.type())) {
			JsonObject body = (JsonObject) jsonObject.get("body");
			// Activity messages always have the username and a message.
			return !body.get("content").getAsString().contains(username);
		}

		return true;
	}
}
