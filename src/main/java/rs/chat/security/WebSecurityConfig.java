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
import rs.chat.utils.Constants;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static rs.chat.router.Routes.LOGIN_URL;
import static rs.chat.router.Routes.OPENED_SESSIONS_URL;
import static rs.chat.router.Routes.REFRESH_TOKEN_URL;
import static rs.chat.router.Routes.REGISTER_URL;
import static rs.chat.router.Routes.ROOT_URL;
import static rs.chat.router.Routes.USERS_URL;
import static rs.chat.router.Routes.USER_SAVE_URL;
import static rs.chat.router.Routes.USER_URL;

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

		studentGETRoutes(http);
		studentPOSTRoutes(http);
		studentPUTRoutes(http);

		teacherGETRoutes(http);
		teacherPOSTRoutes(http);
		teacherPUTRoutes(http);

		adminGETRoutes(http);
		adminPOSTRoutes(http);
		adminPUTRoutes(http);

		http.authorizeRequests()
		    .anyRequest()
		    .authenticated();
	}

	private void studentGETRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(GET, USER_URL, OPENED_SESSIONS_URL)
		    .hasAnyAuthority(Constants.STUDENT_ROLE, Constants.TEACHER_ROLE, Constants.ADMIN_ROLE);
	}

	private void studentPOSTRoutes(HttpSecurity http) throws Exception {
	}

	private void studentPUTRoutes(HttpSecurity http) throws Exception {
	}

	private void teacherGETRoutes(HttpSecurity http) throws Exception {
	}

	private void teacherPOSTRoutes(HttpSecurity http) throws Exception {
	}

	private void teacherPUTRoutes(HttpSecurity http) throws Exception {
	}

	private void adminGETRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(GET, USERS_URL)
		    .authenticated();
	}

	private void adminPOSTRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(POST, USER_SAVE_URL)
		    .hasAuthority(Constants.ADMIN_ROLE);
	}

	private void adminPUTRoutes(HttpSecurity http) {
	}

	private void publicRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(
				    ROOT_URL,
				    LOGIN_URL,
				    REFRESH_TOKEN_URL,
				    REGISTER_URL)
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
