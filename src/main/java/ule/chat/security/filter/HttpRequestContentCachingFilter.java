package ule.chat.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ule.chat.utils.CachedBodyHttpServletRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "ContentCachingFilter", urlPatterns = "/*")
public class HttpRequestContentCachingFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest,
	                                @NotNull HttpServletResponse httpServletResponse,
	                                FilterChain filterChain) throws ServletException, IOException {
		log.info("IN ContentCachingFilter");
		CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(httpServletRequest);
		filterChain.doFilter(cachedBodyHttpServletRequest, httpServletResponse);
	}
}
