package ule.chat.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ule.chat.security.filter.ULEChatAuthenticationFilter;
import ule.chat.security.filter.ULEChatAuthorizationFilter;

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
		http.cors(); //! Enable CORS. In every controller put the @CrossOrigin annotation.
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);

		this.authorizeRequests(http);

		this.addFilters(http);
	}

	private void authorizeRequests(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(LOGIN.url(), REFRESH_TOKEN.url()).permitAll();
		http.authorizeRequests().antMatchers(USER.method(), USER.url()).hasAnyRole(STUDENT_ROLE);
		http.authorizeRequests().antMatchers(USER_SAVE.method(), USER_SAVE.url()).hasAuthority(ADMIN_ROLE);
		http.authorizeRequests().antMatchers(USERS.method(), USERS.url()).authenticated();

		http.authorizeRequests().anyRequest().authenticated();
	}

	private void addFilters(HttpSecurity http) throws Exception {
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
		authenticationFilter.setFilterProcessesUrl(LOGIN.url());
		return authenticationFilter;
	}
}
