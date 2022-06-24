package ule.chat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MaliciousCodeInjectionException extends RuntimeException {
	/**
	 * Creates a new Exception with a default message.
	 *
	 * @param field Field in which the malicious code was injected. <b>Must be <i>First word capitalized</i></b>
	 * @param excludedCharacters list of the blocked characters.
	 */
	public MaliciousCodeInjectionException(String field, List<String> excludedCharacters) {
		super(field + " should not contain the following characters: " + String.join(", ", excludedCharacters));
	}
}
