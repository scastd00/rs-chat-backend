package rs.chat.net.http;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StreamUtils;
import rs.chat.utils.Utils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that wraps an {@link HttpServletRequest} and provides methods to
 * read multiple times the body of the request as a {@link JsonObject}.
 */
public class HttpRequest extends HttpServletRequestWrapper {
	private final byte[] cachedBody;
	private final Map<String, Object> data = new HashMap<>();
	private final JsonObject parsedBody;

	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request the request to wrap.
	 *
	 * @throws IllegalArgumentException if the request is null.
	 */
	public HttpRequest(HttpServletRequest request) throws IOException {
		super(request);
		this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
		this.parsedBody = Utils.parseJson(IOUtils.toString(this.getReader()));
	}

	/**
	 * Gets the cached body as a {@link CachedBodyServletInputStream}.
	 *
	 * @return the cached body as a {@link CachedBodyServletInputStream}.
	 */
	@Override
	public ServletInputStream getInputStream() {
		return new CachedBodyServletInputStream(this.cachedBody);
	}

	/**
	 * Creates a new {@link BufferedReader} for the cached body byte array.
	 *
	 * @return a new {@link BufferedReader} for the cached body.
	 */
	@Override
	public BufferedReader getReader() {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
		return new BufferedReader(new InputStreamReader(byteArrayInputStream));
	}

	/**
	 * Gets the body of the request as a {@link JsonObject}.
	 *
	 * @return the body of the request as a {@link JsonObject}.
	 */
	public JsonObject body() {
		return this.parsedBody;
	}

	/**
	 * Returns the value of the given key in the data attribute of the request and
	 * removes it from the map.
	 *
	 * @param key the key to get the value of.
	 *
	 * @return the value of the given key in the data of the request.
	 *
	 * @implNote This method uses the custom "data" attribute that is present in this
	 * class, but it is not an HTTP standard.
	 */
	public Object get(String key) {
		return this.data.remove(key);
	}

	/**
	 * Adds (or changes) the value of the given key in the data attribute
	 * of the request.
	 *
	 * @param key   the key to set the value of.
	 * @param value the value to set.
	 *
	 * @implNote This method uses the custom "data" attribute that is present in this
	 * class, but it is not an HTTP standard.
	 */
	public void set(String key, Object value) {
		this.data.put(key, value);
	}

	/**
	 * Wrapper class for the cached body.
	 */
	static class CachedBodyServletInputStream extends ServletInputStream {
		private final InputStream cachedBodyInputStream;

		/**
		 * Constructs a new {@link CachedBodyServletInputStream} for the given
		 * byte array body.
		 *
		 * @param cachedBody the byte array body to wrap.
		 */
		public CachedBodyServletInputStream(byte[] cachedBody) {
			this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
		}

		/**
		 * {@inheritDoc}
		 */
		@SneakyThrows(IOException.class)
		@Override
		public boolean isFinished() {
			return this.cachedBodyInputStream.available() == 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isReady() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setReadListener(ReadListener listener) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int read() throws IOException {
			return cachedBodyInputStream.read();
		}
	}
}
