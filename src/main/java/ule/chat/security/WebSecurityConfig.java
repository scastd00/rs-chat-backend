package ule.chat.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import ule.chat.security.filter.ULEChatAuthenticationFilter;
import ule.chat.security.filter.ULEChatAuthorizationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static ule.chat.router.Routes.LOGIN;
import static ule.chat.router.Routes.LOGOUT;
import static ule.chat.router.Routes.REFRESH_TOKEN;
import static ule.chat.router.Routes.USER;
import static ule.chat.router.Routes.USERS;
import static ule.chat.router.Routes.USER_SAVE;
import static ule.chat.utils.Constants.ADMIN_ROLE;
import static ule.chat.utils.Constants.STUDENT_ROLE;

// https://youtu.be/VVn9OG9nfH0?t=2983
// Refresh token -> 1:10:00 approximately.

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder passwordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService)
		    .passwordEncoder(this.passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors(); // Enable CORS.
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);

		http.authorizeRequests().antMatchers(LOGIN.getUrl(), REFRESH_TOKEN.getUrl(), LOGOUT.getUrl()).permitAll();
		http.authorizeRequests().antMatchers(GET, USER.getUrl()).hasAnyRole(STUDENT_ROLE);
		http.authorizeRequests().antMatchers(POST, USER_SAVE.getUrl()).hasAnyAuthority(ADMIN_ROLE);
		http.authorizeRequests().antMatchers(GET, USERS.getUrl()).authenticated();

		http.authorizeRequests().anyRequest().authenticated();

		http.logout().permitAll().logoutSuccessHandler(this.getLogoutSuccessHandler());

		http.addFilter(this.getULEChatCustomAuthenticationFilter());
		http.addFilterBefore(new ULEChatAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	private ULEChatAuthenticationFilter getULEChatCustomAuthenticationFilter() throws Exception {
		ULEChatAuthenticationFilter authenticationFilter =
				new ULEChatAuthenticationFilter(this.authenticationManagerBean());
		authenticationFilter.setFilterProcessesUrl(LOGIN.getUrl());
		return authenticationFilter;
	}

	private LogoutSuccessHandler getLogoutSuccessHandler() {
		return (request, response, authentication) -> {
			String authorization = request.getHeader("Authorization");
			log.info("User logged out. {}", authorization);
			response.setStatus(HttpStatus.OK.value());
		};
	}
}
