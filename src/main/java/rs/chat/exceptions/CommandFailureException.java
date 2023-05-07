package rs.chat.exceptions;

public class CommandFailureException extends RSChatRuntimeException {
	public CommandFailureException(String message) {
		super(message);
	}
}
