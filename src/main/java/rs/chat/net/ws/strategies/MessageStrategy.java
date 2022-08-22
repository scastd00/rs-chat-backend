package rs.chat.net.ws.strategies;

import rs.chat.exceptions.WebSocketException;

public interface MessageStrategy {
	void handle(String message) throws WebSocketException;
}
