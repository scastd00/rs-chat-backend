package rs.chat.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.chat.config.security.filter.AuthenticationFilter;
import rs.chat.config.security.filter.AuthorizationFilter;

import java.time.Clock;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static rs.chat.router.Routes.ACTUATOR_URL;
import static rs.chat.router.Routes.DeleteRoute;
import static rs.chat.router.Routes.GetRoute;
import static rs.chat.router.Routes.GetRoute.HEALTH_URL;
import static rs.chat.router.Routes.PostRoute;
import static rs.chat.router.Routes.PostRoute.CREATE_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.FORGOT_PASSWORD_URL;
import static rs.chat.router.Routes.PostRoute.LOGIN_URL;
import static rs.chat.router.Routes.PostRoute.LOGOUT_URL;
import static rs.chat.router.Routes.PostRoute.REGISTER_URL;
import static rs.chat.router.Routes.PutRoute;
import static rs.chat.router.Routes.ROOT_URL;
import static rs.chat.router.Routes.TEST_URL;
import static rs.chat.router.Routes.WS_CHAT_ENDPOINT;
import static rs.chat.utils.Constants.LOW_TIER_ROLES;
import static rs.chat.utils.Constants.MEDIUM_TIER_ROLES;
import static rs.chat.utils.Constants.STRING_ARRAY;
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
public class WebSecurityConfig {
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final Clock clock;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);
		http.requiresChannel(channel -> channel.anyRequest().requiresSecure());

		this.publicRoutes(http);
		this.privateRoutes(http);
		this.addFilters(http);

		return http.build();
	}

	/**
	 * Registers the routes that are allowed for the <b>authenticated</b> user.
	 *
	 * @param http {@link HttpSecurity} object.
	 *
	 * @throws Exception if an error occurs.
	 */
	private void privateRoutes(HttpSecurity http) throws Exception {
		// Low tier
		registerRoutesOfTier(http, LOW_TIER_ROLES.toArray(STRING_ARRAY),
		                     GetRoute.INSTANCE.lowTierRoutes(), PostRoute.INSTANCE.lowTierRoutes(),
		                     PutRoute.INSTANCE.lowTierRoutes(), DeleteRoute.INSTANCE.lowTierRoutes());
		// Medium tier
		registerRoutesOfTier(http, MEDIUM_TIER_ROLES.toArray(STRING_ARRAY),
		                     GetRoute.INSTANCE.mediumTierRoutes(), PostRoute.INSTANCE.mediumTierRoutes(),
		                     PutRoute.INSTANCE.mediumTierRoutes(), DeleteRoute.INSTANCE.mediumTierRoutes());
		// Top tier
		registerRoutesOfTier(http, TOP_TIER_ROLES.toArray(STRING_ARRAY),
		                     GetRoute.INSTANCE.topTierRoutes(), PostRoute.INSTANCE.topTierRoutes(),
		                     PutRoute.INSTANCE.topTierRoutes(), DeleteRoute.INSTANCE.topTierRoutes());

		http.authorizeHttpRequests()
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
		new RouterSecurityConfig(http, allowedRoles)
				.registerRoutes(HttpMethod.GET, getRoutes)
				.registerRoutes(HttpMethod.POST, postRoutes)
				.registerRoutes(HttpMethod.PUT, putRoutes)
				.registerRoutes(HttpMethod.DELETE, deleteRoutes);
	}

	/**
	 * Registers all the public routes. These routes do not need authentication.
	 *
	 * @param http {@link HttpSecurity} object.
	 *
	 * @throws Exception if an error occurs.
	 */
	private void publicRoutes(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()
		    .requestMatchers(
				    ROOT_URL, LOGIN_URL,
				    LOGOUT_URL, REGISTER_URL,
				    WS_CHAT_ENDPOINT, FORGOT_PASSWORD_URL,
				    CREATE_PASSWORD_URL, HEALTH_URL,
				    ACTUATOR_URL, TEST_URL
		    )
		    .permitAll();
	}

	/**
	 * Adds the needed filters.
	 *
	 * @param http {@link HttpSecurity} object.
	 */
	private void addFilters(HttpSecurity http) {
		http.addFilter(this.getCustomAuthenticationFilter());
		http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(List.of(this.authenticationProvider()));
	}

	/**
	 * Creates the {@link AuthenticationFilter} that is used by the application.
	 *
	 * @return {@link AuthenticationFilter} object.
	 */
	private AuthenticationFilter getCustomAuthenticationFilter() {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(
				this.authenticationManager(),
				this.clock
		);
		authenticationFilter.setFilterProcessesUrl(LOGIN_URL);
		return authenticationFilter;
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.userDetailsService);
		provider.setPasswordEncoder(this.passwordEncoder);
		return provider;
	}
}
