package ule.chat.net;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HttpResponse extends HttpServletResponseWrapper {
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
}
