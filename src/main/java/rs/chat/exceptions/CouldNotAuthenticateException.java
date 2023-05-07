package rs.chat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CouldNotAuthenticateException extends RSChatRuntimeException {
	public CouldNotAuthenticateException(String message) {
		super(message);
	}
}
