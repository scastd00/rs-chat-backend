package rs.chat.controllers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;
import java.util.function.UnaryOperator;

import static rs.chat.utils.Constants.ERROR_JSON_KEY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ControllerUtils {
	public static Object performActionThatMayThrow(HttpResponse response, UnaryOperator<Object> action)
			throws IOException {
		try {
			return action.apply(null);
		} catch (Exception e) {
			// Get the status of the exception with reflection
			ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);

			if (annotation != null) {
				// If the exception has a status, set it to the response
				response.status(annotation.value());
			} else {
				// If the exception does not have a status, set the response to 500
				response.status(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			response.send(ERROR_JSON_KEY, e.getMessage());
			throw e; // Exit from executing the rest of the controller method.
		}
	}
}
