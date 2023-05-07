package rs.chat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NicknameAlreadyInUseException extends RSChatRuntimeException {
	public NicknameAlreadyInUseException(String message) {
		super(message);
	}
}
