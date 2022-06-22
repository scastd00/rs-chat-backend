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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ule.chat.security.filter.ULEChatAuthenticationFilter;
import ule.chat.security.filter.ULEChatAuthorizationFilter;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static ule.chat.router.Routes.LOGIN;
import static ule.chat.router.Routes.REFRESH_TOKEN;
import static ule.chat.router.Routes.REGISTER;
import static ule.chat.router.Routes.ROOT;
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
@SuppressWarnings("deprecation")
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
		http.cors();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);

		this.authorizeRequests(http);
		this.addFilters(http);
	}

	private void authorizeRequests(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(
				    ROOT.url(),
				    LOGIN.url(),
				    REFRESH_TOKEN.url(),
				    REGISTER.url())
		    .permitAll();

		// Public routes
		http.authorizeRequests()
		    .antMatchers(USER.method(), USER.url())
		    .hasAnyRole(STUDENT_ROLE);

		// Admin routes
		http.authorizeRequests()
		    .antMatchers(USER_SAVE.method(), USER_SAVE.url())
		    .hasAuthority(ADMIN_ROLE);

		// Private routes
		http.authorizeRequests()
		    .antMatchers(USERS.method(), USERS.url())
		    .authenticated();

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

//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		final CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(List.of("https://spring-chat-rs.herokuapp.com/"));
//		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
//		configuration.setAllowedHeaders(List.of("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
//				"Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin",
//				"Cache-Control", "Content-Type"));
//		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", configuration);
//		return source;
//	}
}
