package rs.chat.net.ws;

import lombok.Getter;
import lombok.SneakyThrows;
import rs.chat.storage.MessageWriter;

import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Chat {
	private final CopyOnWriteArrayList<WSClient> clients;
	private final MessageWriter writer;

	@SneakyThrows
	public Chat(String chatId) {
		this.clients = new CopyOnWriteArrayList<>();
		this.writer = new MessageWriter(chatId);
	}

	public void finish() {
		this.writer.close();
	}
}
