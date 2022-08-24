package rs.chat.config.security;

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
import rs.chat.config.security.filter.RSChatAuthenticationFilter;
import rs.chat.config.security.filter.RSChatAuthorizationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static rs.chat.router.Routes.DeleteRoute;
import static rs.chat.router.Routes.GetRoute;
import static rs.chat.router.Routes.PostRoute;
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.LOGOUT_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;
import static rs.chat.router.Routes.PutRoute;
import static rs.chat.router.Routes.REFRESH_TOKEN_URL;
import static rs.chat.router.Routes.ROOT_URL;
import static rs.chat.router.Routes.WS_CHAT_ENDPOINT;
import static rs.chat.utils.Constants.LOW_TIER_ROLES;
import static rs.chat.utils.Constants.MEDIUM_TIER_ROLES;
import static rs.chat.utils.Constants.TOP_TIER_ROLES;

// https://youtu.be/VVn9OG9nfH0?t=2983
// Refresh token -> 1:10:00 approximately.

/**
 * Class that configures security for all the application.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("deprecation")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder passwordEncoder;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService)
		    .passwordEncoder(this.passwordEncoder);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Enables CORS and disables CSRF for the whole application, configures
	 * all the routes that are allowed for the user and adds the needed filters.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);

		this.authorizeRequests(http);
		this.addFilters(http);
	}

	/**
	 * Registers all the routes that are allowed for the user.
	 *
	 * @param http {@link HttpSecurity} object.
	 *
	 * @throws Exception if an error occurs.
	 */
	private void authorizeRequests(HttpSecurity http) throws Exception {
		publicRoutes(http);

		// Low tier
		registerRoutesOfTier(http, LOW_TIER_ROLES,
		                     GetRoute.INSTANCE.lowTierRoutes(), PostRoute.INSTANCE.lowTierRoutes(),
		                     PutRoute.INSTANCE.lowTierRoutes(), DeleteRoute.INSTANCE.lowTierRoutes());
		// Medium tier
		registerRoutesOfTier(http, MEDIUM_TIER_ROLES,
		                     GetRoute.INSTANCE.mediumTierRoutes(), PostRoute.INSTANCE.mediumTierRoutes(),
		                     PutRoute.INSTANCE.mediumTierRoutes(), DeleteRoute.INSTANCE.mediumTierRoutes());
		// Top tier
		registerRoutesOfTier(http, TOP_TIER_ROLES,
		                     GetRoute.INSTANCE.topTierRoutes(), PostRoute.INSTANCE.topTierRoutes(),
		                     PutRoute.INSTANCE.topTierRoutes(), DeleteRoute.INSTANCE.topTierRoutes());

		http.authorizeRequests()
		    .anyRequest()
		    .authenticated();
	}

	/**
	 * Generalizes the registration of the routes for a concrete tier.
	 *
	 * @param http         {@link HttpSecurity} object.
	 * @param allowedRoles {@link String} array of the allowed roles for the specified routes.
	 * @param getRoutes    {@link GetRoute} array of get routes.
	 * @param postRoutes   {@link PostRoute} array of post routes.
	 * @param putRoutes    {@link PutRoute} array of put routes.
	 * @param deleteRoutes {@link DeleteRoute} array of delete routes.
	 *
	 * @throws Exception if an error occurs.
	 */
	private static void registerRoutesOfTier(HttpSecurity http,
	                                         String[] allowedRoles,
	                                         String[] getRoutes,
	                                         String[] postRoutes,
	                                         String[] putRoutes,
	                                         String[] deleteRoutes) throws Exception {
		RouterSecurityConfig routerSecurityConfig = new RouterSecurityConfig(http, allowedRoles);

		routerSecurityConfig
				.addGETRoutes(getRoutes)
				.addPOSTRoutes(postRoutes)
				.addPUTRoutes(putRoutes)
				.addDELETERoutes(deleteRoutes)
				.registerRoutes();
	}

	/**
	 * Registers all the public routes.
	 *
	 * @param http {@link HttpSecurity} object.
	 *
	 * @throws Exception if an error occurs.
	 */
	private void publicRoutes(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers(
				    ROOT_URL,
				    LOGIN_URL,
				    LOGOUT_URL,
				    REFRESH_TOKEN_URL,
				    REGISTER_URL,
				    WS_CHAT_ENDPOINT
		    )
		    .permitAll();
	}

	/**
	 * Adds the needed filters.
	 *
	 * @param http {@link HttpSecurity} object.
	 *
	 * @throws Exception if an error occurs.
	 */
	private void addFilters(HttpSecurity http) throws Exception {
		http.addFilter(this.getRSChatCustomAuthenticationFilter());
		http.addFilterBefore(new RSChatAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/**
	 * Creates the {@link AuthenticationManager} that is used by the application.
	 *
	 * @return {@link AuthenticationManager} object.
	 *
	 * @throws Exception if an error occurs.
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * Creates the {@link RSChatAuthenticationFilter} that is used by the application.
	 *
	 * @return {@link RSChatAuthenticationFilter} object.
	 *
	 * @throws Exception if an error occurs.
	 */
	private RSChatAuthenticationFilter getRSChatCustomAuthenticationFilter() throws Exception {
		RSChatAuthenticationFilter authenticationFilter =
				new RSChatAuthenticationFilter(this.authenticationManagerBean());
		authenticationFilter.setFilterProcessesUrl(LOGIN_URL);
		return authenticationFilter;
	}
}
