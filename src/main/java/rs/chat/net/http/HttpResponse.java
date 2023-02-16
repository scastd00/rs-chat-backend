package rs.chat.net.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {
	public static HttpServletResponse status(HttpServletResponse response, @NotNull HttpStatus status) {
		response.setStatus(status.value());
		return response;
	}

	public static HttpServletResponse ok(HttpServletResponse response) {
		return status(response, HttpStatus.OK);
	}

	public static HttpServletResponse created(HttpServletResponse response, String requestURL) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
		                                                .path(requestURL)
		                                                .toUriString());
		response.setHeader(LOCATION, uri.toString());
		return status(response, HttpStatus.CREATED);
	}

	public static HttpServletResponse badRequest(HttpServletResponse response) {
		return status(response, HttpStatus.BAD_REQUEST);
	}

	public static HttpServletResponse notFound(HttpServletResponse response) {
		return status(response, HttpStatus.NOT_FOUND);
	}

	public static void sendStatus(HttpServletResponse response, HttpStatus status) throws IOException {
		send(status(response, status));
	}

	public static void send(HttpServletResponse response) throws IOException {
		send(response, HttpResponseBody.EMPTY);
	}

	public static void send(HttpServletResponse response, Object content) throws IOException {
		if (HttpStatus.valueOf(response.getStatus()).isError()) {
			send(response, new HttpResponseBody(ERROR_JSON_KEY, content));
		} else {
			send(response, new HttpResponseBody(DATA_JSON_KEY, content));
		}
	}

	public static void send(HttpServletResponse response, HttpResponseBody body) throws IOException {
		if (HttpStatus.resolve(response.getStatus()) == null) {
			log.error("Http response status must not be null");
			throw new InternalServerException("Please try again later.");
		}

		response.setContentType(APPLICATION_JSON_VALUE);

		if (body == HttpResponseBody.EMPTY) {
			response.getWriter().print(body.value()); // Empty string
			return;
		}

		JsonElement responseBody = body.data;

		// If the response body contains only one element, send it directly (without the key)
		// to simplify the json sent to the client.
		if (body.data.size() == 1) {
			responseBody = body.value();
		}

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
		 * @return the value of the first element in the body.
		 */
		public JsonElement value() {
			return this.data.asMap().values().iterator().next();
		}
	}
}
