package rs.chat.exceptions;

public class CommandFailureException extends RuntimeException {
	public CommandFailureException(String message) {
		super(message);
	}
}
