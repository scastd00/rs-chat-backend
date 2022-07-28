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

public class HttpRequest extends HttpServletRequestWrapper {
	private final byte[] cachedBody;
	private final Map<String, Object> data = new HashMap<>();

	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request The request to wrap.
	 *
	 * @throws IllegalArgumentException if the request is null.
	 */
	public HttpRequest(HttpServletRequest request) throws IOException {
		super(request);
		InputStream in = request.getInputStream();
		this.cachedBody = StreamUtils.copyToByteArray(in);
	}

	@Override
	public ServletInputStream getInputStream() {
		return new CachedBodyServletInputStream(this.cachedBody);
	}

	@Override
	public BufferedReader getReader() {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
		return new BufferedReader(new InputStreamReader(byteArrayInputStream));
	}

	public JsonObject body() throws IOException {
		return Utils.parseJson(IOUtils.toString(this.getReader()));
	}

	public Object get(String key) {
		return this.data.remove(key);
	}

	public void set(String key, Object value) {
		this.data.put(key, value);
	}

	static class CachedBodyServletInputStream extends ServletInputStream {
		private final InputStream cachedBodyInputStream;

		public CachedBodyServletInputStream(byte[] cachedBody) {
			this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
		}

		@SneakyThrows(IOException.class)
		@Override
		public boolean isFinished() {
			return this.cachedBodyInputStream.available() == 0;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int read() throws IOException {
			return cachedBodyInputStream.read();
		}
	}
}