package rs.chat.exceptions;

public class CommandUnavailableException extends RuntimeException {
	public CommandUnavailableException(String message) {
		super(message);
	}
}
