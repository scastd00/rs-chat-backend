package rs.chat.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.chat.exceptions.InternalServerException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
	 * @return
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
	 * Sends a response with one element in its content.
	 *
	 * @param key   the key of the element to be sent.
	 * @param value the value of the element to be sent.
	 *
	 * @throws IOException if an error occurs while sending the response.
	 */
	public void send(String key, Object value) throws IOException {
		this.send(new HttpResponseBody(key, value));
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
			this.getWriter().print(""); // Empty body
		} else {
			this.objectMapper.writeValue(this.getWriter(), response.data);
		}
	}

	/**
	 * Class that represents the body of a response.
	 * Contains a {@link Map} of elements to be sent in the response.
	 */
	@NoArgsConstructor
	public static class HttpResponseBody {
		private final Map<String, Object> data = new HashMap<>();
		public static final HttpResponseBody EMPTY = null;

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
	}
}
