package rs.chat.security;

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
import rs.chat.security.filter.RSChatAuthenticationFilter;
import rs.chat.security.filter.RSChatAuthorizationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static rs.chat.router.Routes.CHANGE_PASSWORD_URL;
import static rs.chat.router.Routes.CHAT_CONTENT_URL;
import static rs.chat.router.Routes.CHAT_METADATA_URL;
import static rs.chat.router.Routes.LOGIN_URL;
import static rs.chat.router.Routes.OPENED_SESSIONS_URL;
import static rs.chat.router.Routes.REFRESH_TOKEN_URL;
import static rs.chat.router.Routes.REGISTER_URL;
import static rs.chat.router.Routes.ROOT_URL;
import static rs.chat.router.Routes.USERS_URL;
import static rs.chat.router.Routes.USER_SAVE_URL;
import static rs.chat.router.Routes.USER_URL;
import static rs.chat.utils.Constants.LOW_TIER_ROLES;
import static rs.chat.utils.Constants.TOP_TIER_ROLES;

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
		publicRoutes(http);

		// Private routes

		lowTierGETRoutes(http);
		lowTierPOSTRoutes(http);
		lowTierPUTRoutes(http);

		mediumTierGETRoutes(http);
		mediumTierPOSTRoutes(http);
		mediumTierPUTRoutes(http);

		topTierGETRoutes(http);
		topTierPOSTRoutes(http);
		topTierPUTRoutes(http);

		http.authorizeRequests()
		    .anyRequest()
		    .authenticated();
	}

	private void lowTierGETRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(GET, USER_URL, OPENED_SESSIONS_URL, CHAT_METADATA_URL, CHAT_CONTENT_URL)
		    .hasAnyAuthority(LOW_TIER_ROLES);
	}

	private void lowTierPOSTRoutes(HttpSecurity http) throws Exception {
	}

	private void lowTierPUTRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(PUT, CHANGE_PASSWORD_URL)
		    .hasAnyAuthority(LOW_TIER_ROLES);
	}

	private void mediumTierGETRoutes(HttpSecurity http) throws Exception {
	}

	private void mediumTierPOSTRoutes(HttpSecurity http) throws Exception {
	}

	private void mediumTierPUTRoutes(HttpSecurity http) throws Exception {
	}

	private void topTierGETRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(GET, USERS_URL)
		    .hasAnyAuthority(TOP_TIER_ROLES);
	}

	private void topTierPOSTRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(POST, USER_SAVE_URL)
		    .hasAnyAuthority(TOP_TIER_ROLES);
	}

	private void topTierPUTRoutes(HttpSecurity http) {
	}

	private void publicRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(
				    ROOT_URL,
				    LOGIN_URL,
				    REFRESH_TOKEN_URL,
				    REGISTER_URL
		    )
		    .permitAll();
	}

	private void addFilters(HttpSecurity http) throws Exception {
		http.addFilter(this.getRSChatCustomAuthenticationFilter());
		http.addFilterBefore(new RSChatAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	private RSChatAuthenticationFilter getRSChatCustomAuthenticationFilter() throws Exception {
		RSChatAuthenticationFilter authenticationFilter =
				new RSChatAuthenticationFilter(this.authenticationManagerBean());
		authenticationFilter.setFilterProcessesUrl(LOGIN_URL);
		return authenticationFilter;
	}
}
