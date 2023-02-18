package rs.chat.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.function.Function0;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import rs.chat.net.http.HttpResponse;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ControllerUtils {
	/**
	 * Performs the given function and sends the result to the response.
	 * If an error occurs, the response is sent with the error message. When an exception is caught,
	 * it is checked if it is annotated with {@link ResponseStatus}. If it is, the response is sent
	 * with the status code and message from the annotation. If it is not, the response is sent with the status
	 * {@link HttpStatus#BAD_GATEWAY} (502) and the exception message. In both cases, the exception is logged
	 * and rethrown, to prevent the controller from executing more code.
	 *
	 * @param res    response to send the result to.
	 * @param action function to perform.
	 * @param <R>    type of the result.
	 *
	 * @return the result of the function.
	 *
	 * @throws IOException if an error occurs.
	 */
	public static <R> R performActionThatMayThrowException(HttpServletResponse res, Function0<R> action)
			throws IOException {
		try {
			return action.apply();
		} catch (Exception e) {
			// Get the status of the exception with reflection
			ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);

			// If the exception does not have a status, set the response to 502
			// to indicate that the handled exception does not have the annotation.
			HttpStatus status = annotation == null
			                    ? HttpStatus.BAD_GATEWAY
			                    : annotation.value(); // If the exception has a status, set it to the response

			new HttpResponse(res).status(status).send(e.getMessage());
			log.error("Error while performing action", e);
			throw e; // Exit from executing the rest of the controller method.
		}
	}
}
