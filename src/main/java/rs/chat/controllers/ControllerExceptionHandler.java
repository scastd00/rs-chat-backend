package rs.chat.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import rs.chat.exceptions.RSChatRuntimeException;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

@ControllerAdvice
@NoArgsConstructor
@Slf4j
public class ControllerExceptionHandler {
	/**
	 * Handles exceptions thrown by the controllers. When an exception is caught,
	 * it is checked if it is annotated with {@link ResponseStatus}.
	 * If it is present, the response is sent with the status code of the annotation and message of the exception.
	 * If it is not present, the response is sent with the status {@link HttpStatus#BAD_GATEWAY}
	 * (502) and the exception message.
	 *
	 * @param e        exception to handle.
	 * @param response response to send the error to.
	 *
	 * @throws IOException if an error occurs while sending the response.
	 */
	@ExceptionHandler(RSChatRuntimeException.class)
	public void handleException(RSChatRuntimeException e, HttpServletResponse response) throws IOException {
		// Get the status of the exception with reflection
		ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);

		// If the exception does not have a status, set the response to 502
		// to indicate that the handled exception does not have the annotation.
		HttpStatus status = annotation == null
		                    ? HttpStatus.BAD_GATEWAY
		                    : annotation.value(); // If the exception has a status, set it to the response

		new HttpResponse(response).status(status).send(e.getMessage());
		log.error("Error while performing action", e);
	}
}
