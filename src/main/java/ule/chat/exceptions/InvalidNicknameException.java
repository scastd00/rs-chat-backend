package ule.chat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidNicknameException extends RuntimeException {
	public InvalidNicknameException(String message) {
		super(message);
	}
}
