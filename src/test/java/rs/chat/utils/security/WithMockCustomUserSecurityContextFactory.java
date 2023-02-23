package rs.chat.utils.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import rs.chat.utils.security.annotations.WithMockCustomUser;

import java.util.Arrays;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		UserDetails principal = new User(
				customUser.username(),
				customUser.password(),
				Arrays.stream(customUser.roles()).map(SimpleGrantedAuthority::new).toList()
		);

		Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
				principal,
				principal.getPassword(),
				principal.getAuthorities()
		);
		context.setAuthentication(authentication);

		return context;
	}
}
