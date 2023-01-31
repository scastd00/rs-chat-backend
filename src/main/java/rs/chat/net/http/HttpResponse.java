package rs.chat.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.chat.exceptions.InternalServerException;
import rs.chat.utils.Constants;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rs.chat.utils.Constants.DATA_JSON_KEY;
import static rs.chat.utils.Constants.ERROR_JSON_KEY;

/**
 * Class that simplifies the management of the response to the client.
 */
@Slf4j
public class HttpResponse extends HttpServletResponseWrapper {
	private HttpStatus status = null;
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	/**
	 * Constructs a response adaptor wrapping the given response.
	 *
	 * @param response the response to be wrapped.
	 *
	 * @throws IllegalArgumentException if the response is null.
	 */
	public HttpResponse(HttpServletResponse response) {
		super(response);
	}

	/**
	 * Sets the status of the response.
	 *
	 * @param status the status to be set.
	 *
	 * @return this response with the status set.
	 */
	public HttpResponse status(@NotNull HttpStatus status) {
		this.status = status;
		this.setStatus(status.value());
		return this;
	}

	/**
	 * Sets the status of the response to {@link HttpStatus#OK}.
	 *
	 * @return this response with the status set.
	 */
	public HttpResponse ok() {
		return this.status(HttpStatus.OK);
	}

	/**
	 * Sets the status of the response to {@link HttpStatus#CREATED}.
	 * This method also sets the Location header to the location of the created resource.
	 *
	 * @param requestURL the URL of the request to set the Location header path.
	 *
	 * @return this response with the status set.
	 */
	public HttpResponse created(String requestURL) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
		                                                .path(requestURL)
		                                                .toUriString());
		this.setHeader("Location", uri.toString());
		return this.status(HttpStatus.CREATED);
	}

	/**
	 * Sets the status of the response to {@link HttpStatus#BAD_REQUEST}.
	 *
	 * @return this response with the status set.
	 */
	public HttpResponse badRequest() {
		return this.status(HttpStatus.BAD_REQUEST);
	}

	/**
	 * Sets the status of the response to {@link HttpStatus#NOT_FOUND}.
	 *
	 * @return this response with the status set.
	 */
	public HttpResponse notFound() {
		return this.status(HttpStatus.NOT_FOUND);
	}

	/**
	 * Immediately sends a response with the given status and empty body.
	 *
	 * @param status the status to be set.
	 *
	 * @throws IOException if an error occurs while sending the response.
	 */
	public void sendStatus(HttpStatus status) throws IOException {
		this.status(status).send(HttpResponseBody.EMPTY);
	}

	/**
	 * Immediately sends a response with the given status and empty body.
	 *
	 * @throws IOException if an error occurs while sending the response.
	 */
	public void send() throws IOException {
		this.send(HttpResponseBody.EMPTY);
	}

	/**
	 * Sends the given body to the client.
	 * <p>
	 * If it is an error, the key is set to {@link Constants#ERROR_JSON_KEY}.
	 * If not, the key is set to {@link Constants#DATA_JSON_KEY}.
	 *
	 * @param content the content to be sent.
	 *
	 * @throws IOException if an error occurs while sending the response.
	 */
	public void send(Object content) throws IOException {
		if (this.status.isError()) {
			this.send(new HttpResponseBody(ERROR_JSON_KEY, content));
		} else {
			this.send(new HttpResponseBody(DATA_JSON_KEY, content));
		}
	}

	/**
	 * Sends a response with the given body.
	 *
	 * @param response the body of the response to be sent.
	 *
	 * @throws IOException if an error occurs while sending the response.
	 */
	public void send(HttpResponseBody response) throws IOException {
		if (this.status == null) {
			log.error("Http response status must not be null");
			throw new InternalServerException("Please try again later.");
		}

		this.setContentType(APPLICATION_JSON_VALUE);

		if (response == HttpResponseBody.EMPTY) {
			this.getWriter().print(response.value()); // Empty string
			return;
		}

		Object responseBody = response.data;

		// If the response body contains only one element, send it directly (without the key)
		if (response.data.size() == 1) {
			responseBody = response.value();
		}

		this.objectMapper.writeValue(this.getWriter(), responseBody);
	}

	/**
	 * Class that represents the body of a response.
	 * Contains a {@link Map} of elements to be sent in the response.
	 */
	public static class HttpResponseBody {
		private final Map<String, Object> data = new HashMap<>();
		public static final HttpResponseBody EMPTY = new HttpResponseBody(DATA_JSON_KEY, "");

		/**
		 * Constructs a response body with the given key and value.
		 *
		 * @param key   the key of the element to be sent.
		 * @param value the value of the element to be sent.
		 */
		public HttpResponseBody(String key, Object value) {
			this.add(key, value);
		}

		/**
		 * Adds an element to the body.
		 *
		 * @param key   the key of the element to be added.
		 * @param value the value of the element to be added.
		 *
		 * @return this response body with the element added.
		 */
		public HttpResponseBody add(String key, Object value) {
			this.data.put(key, value);
			return this;
		}

		/**
		 * @return the value of the first element in the body.
		 */
		public Object value() {
			return this.data.values().iterator().next();
		}
	}
}
