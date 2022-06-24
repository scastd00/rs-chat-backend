package ule.chat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MaliciousCodeInjectionException extends RuntimeException {
	public MaliciousCodeInjectionException(String message) {
		super(message);
	}
}
