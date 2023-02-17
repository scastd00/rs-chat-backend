package rs.chat.net.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.chat.exceptions.InternalServerException;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rs.chat.utils.Constants.DATA_JSON_KEY;
import static rs.chat.utils.Constants.ERROR_JSON_KEY;
import static rs.chat.utils.Constants.GSON;
import static rs.chat.utils.Constants.OBJECT_MAPPER;

/**
 * Class that contains methods for sending responses to the client.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {
	/**
	 * Method that assigns to a response the status code 201 (Created) and the location of the created resource.
	 *
	 * @param response   The response to send.
	 * @param requestURL The URL of the created resource.
	 *
	 * @return The response with the status code 201 and the location of the created resource.
	 */
	public static HttpServletResponse created(HttpServletResponse response, String requestURL) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
		                                                .path(requestURL)
		                                                .toUriString());
		response.setHeader(LOCATION, uri.toString());
		response.setStatus(HttpStatus.CREATED.value());
		return response;
	}

	/**
	 * Method that sends a response with the specified status code and an empty body.
	 *
	 * @param response The response to send.
	 * @param status   The status code of the response.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public static void sendStatus(HttpServletResponse response, HttpStatus status) throws IOException {
		send(response, status, HttpResponseBody.EMPTY);
	}

	/**
	 * Method that sends a response with the specified status code and body.
	 *
	 * @param response The response to send.
	 * @param status   The status code of the response.
	 * @param content  The body of the response.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public static void send(HttpServletResponse response, HttpStatus status, Object content) throws IOException {
		if (status.isError()) {
			send(response, new HttpResponseBody(ERROR_JSON_KEY, content));
		} else {
			send(response, new HttpResponseBody(DATA_JSON_KEY, content));
		}
	}

	/**
	 * Method that sends a response with the specified status code and body.
	 *
	 * @param response The response to send.
	 * @param body     The body of the response.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	private static void send(HttpServletResponse response, HttpResponseBody body) throws IOException {
		if (HttpStatus.resolve(response.getStatus()) == null) {
			log.error("Http response status must not be null");
			throw new InternalServerException("Please try again later.");
		}

		response.setContentType(APPLICATION_JSON_VALUE);

		if (body == HttpResponseBody.EMPTY) {
			// This is done like this to send a completely empty response body.
			response.getWriter().print(body.value()); // Empty string
			return;
		}

		JsonElement responseBody = body.data;

		// If the response body contains only one element, send it directly (without the key)
		// to simplify the json sent to the client.
		if (body.data.size() == 1) {
			responseBody = body.value();
		}

		// Serialize the response body to json and send it to the client.
		OBJECT_MAPPER.writeValue(response.getWriter(), responseBody);
	}

	/**
	 * Class that represents the body of a response.
	 * Contains a {@link Map} of elements to be sent in the response.
	 */
	public static class HttpResponseBody {
		public static final HttpResponseBody EMPTY = new HttpResponseBody(DATA_JSON_KEY, "");
		private final JsonObject data = new JsonObject();

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
			this.data.add(key, GSON.toJsonTree(value));
			return this;
		}

		/**
		 * Used when the response body contains only one element.
		 *
		 * @return the value of the first element in the body.
		 */
		public JsonElement value() {
			return this.data.asMap().values().iterator().next();
		}
	}
}
