package ule.chat.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ule.chat.router.Routes.LOGOUT;

@Slf4j
public class ULEChatLogoutFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                @NotNull HttpServletResponse response,
	                                @NotNull FilterChain filterChain) throws ServletException, IOException {
		if (request.getServletPath().equals(LOGOUT.getUrl())) {
			log.info("User is logging out");
			new SecurityContextLogoutHandler().logout(request, null, null);
			filterChain.doFilter(request, response);
		}
	}
}
