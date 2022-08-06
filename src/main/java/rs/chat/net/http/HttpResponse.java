package rs.chat.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import rs.chat.exceptions.InternalServerException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class HttpResponse extends HttpServletResponseWrapper {
	private HttpStatus status = null;
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	/**
	 * Constructs a response adaptor wrapping the given response.
	 *
	 * @param response The response to be wrapped.
	 *
	 * @throws IllegalArgumentException if the response is null.
	 */
	public HttpResponse(HttpServletResponse response) {
		super(response);
	}

	public HttpResponse status(@NotNull HttpStatus status) {
		this.status = status;
		this.setStatus(status.value());
		return this;
	}

	public void sendStatus(HttpStatus status) throws IOException {
		this.status(status).send();
	}

	public void send() throws IOException {
		this.send(HttpResponseBody.EMPTY);
	}

	public void send(String key, Object value) throws IOException {
		this.send(new HttpResponseBody(key, value));
	}

	public void send(HttpResponseBody response) throws IOException {
		if (this.status == null) {
			log.error("Http response status must not be null");
			throw new InternalServerException("Please try again later.");
		}

		this.setContentType(APPLICATION_JSON_VALUE);

		if (response == HttpResponseBody.EMPTY) {
			this.getWriter().print(""); // Empty body
		} else {
			this.objectMapper.writeValue(this.getWriter(), response.getData());
		}
	}

	@NoArgsConstructor
	@Getter
	public static class HttpResponseBody {
		private final Map<String, Object> data = new HashMap<>();
		public static final HttpResponseBody EMPTY = null;

		public HttpResponseBody(String key, Object value) {
			this.add(key, value);
		}

		public HttpResponseBody add(String key, Object value) {
			this.data.put(key, value);
			return this;
		}
	}
}
