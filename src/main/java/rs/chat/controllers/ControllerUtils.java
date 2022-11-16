package rs.chat.controllers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import rs.chat.net.http.HttpResponse;
import rs.chat.utils.NoParamFunction;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ControllerUtils {
	public static <R> R performActionThatMayThrowException(HttpResponse response, NoParamFunction<R> action)
			throws IOException {
		try {
			return action.apply();
		} catch (Exception e) {
			// Get the status of the exception with reflection
			ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);

			if (annotation != null) {
				// If the exception has a status, set it to the response
				response.status(annotation.value());
			} else {
				// If the exception does not have a status, set the response to 502
				// to indicate that the handled exception does not have the annotation.
				response.status(HttpStatus.BAD_GATEWAY);
			}

			response.send(e.getMessage());
			log.error("Error while performing action", e);
			throw e; // Exit from executing the rest of the controller method.
		}
	}
}
