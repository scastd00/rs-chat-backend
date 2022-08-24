package rs.chat.config.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.chat.net.http.HttpRequest;
import rs.chat.net.http.HttpResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class that caches the request received in the server. This allows to
 * read the body of the request multiple times.
 * <p>
 * This filter is ordered {@link Ordered#HIGHEST_PRECEDENCE} so that it is
 * executed before any other filter (when an HTTP request enters the server).
 * <p>
 * <b>This is the first entry point of all the requests that arrive to the
 * server.</b>
 */
@Component
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "ContentCachingFilter", urlPatterns = "/*")
public class HttpRequestContentCachingFilter extends OncePerRequestFilter {
	/**
	 * Wraps the request and the response into an {@link HttpRequest} and
	 * {@link HttpResponse} objects, respectively. This allows to read the body
	 * of the request multiple times.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request,
	                                @NotNull HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {
		filterChain.doFilter(new HttpRequest(request), new HttpResponse(response));
	}
}
