package ule.chat.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import ule.chat.exceptions.InternalServerException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class HttpResponse extends HttpServletResponseWrapper {
	private HttpStatus status = null;

	/**
	 * Constructs a response adaptor wrapping the given response.
	 *
	 * @param response The response to be wrapped.
	 *
	 * @throws IllegalArgumentException if the response is null.
	 */
	public HttpResponse(HttpServletResponse response) {
		super(response);
		this.setContentType(APPLICATION_JSON_VALUE);
	}

	public HttpResponse status(@NotNull HttpStatus status) {
		this.status = status;
		this.setStatus(status.value());
		return this;
	}

	public void sendStatus(@NotNull HttpStatus status) throws IOException {
		this.status(status).send(new HttpResponseBody());
	}

	public void send(HttpResponseBody response) throws IOException {
		if (this.status == null) {
			log.error("Http response status must not be null");
			throw new InternalServerException("Please try again later.");
		}

		new ObjectMapper().writeValue(this.getWriter(), response.getData());
	}
}
