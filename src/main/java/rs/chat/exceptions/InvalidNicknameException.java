package rs.chat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidNicknameException extends RSChatRuntimeException {
	public InvalidNicknameException(String message) {
		super(message);
	}
}
