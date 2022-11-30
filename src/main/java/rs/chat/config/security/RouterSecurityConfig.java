package rs.chat.config.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

/**
 * Class that configures all the routes that the application can handle.
 */
public class RouterSecurityConfig {
	private final HttpSecurity http;
	private final String[] authorizedRoles;

	/**
	 * Constructor that initializes the class with the http security configuration.
	 *
	 * @param http            the http security configuration.
	 * @param authorizedRoles the authorized roles for the routes.
	 */
	public RouterSecurityConfig(HttpSecurity http, String[] authorizedRoles) {
		this.http = http;
		this.authorizedRoles = authorizedRoles;
	}

	/**
	 * Method that configures the GET routes that can be accessed by the user.
	 *
	 * @param urls the urls that can be accessed by the user.
	 *
	 * @return the router security config.
	 */
	public RouterSecurityConfig registerGETRoutes(String... urls) throws Exception {
		return this.registerRoutes(GET, urls);
	}

	/**
	 * Method that configures the POST routes that can be accessed by the user.
	 *
	 * @param urls the urls that can be accessed by the user.
	 *
	 * @return the router security config.
	 */
	public RouterSecurityConfig registerPOSTRoutes(String... urls) throws Exception {
		return this.registerRoutes(POST, urls);
	}

	/**
	 * Method that configures the PUT routes that can be accessed by the user.
	 *
	 * @param urls the urls that can be accessed by the user.
	 *
	 * @return the router security config.
	 */
	public RouterSecurityConfig registerPUTRoutes(String... urls) throws Exception {
		return this.registerRoutes(PUT, urls);
	}

	/**
	 * Method that configures the DELETE routes that can be accessed by the user.
	 *
	 * @param urls the urls that can be accessed by the user.
	 *
	 * @return the router security config.
	 */
	public RouterSecurityConfig registerDELETERoutes(String... urls) throws Exception {
		return this.registerRoutes(DELETE, urls);
	}

	/**
	 * Method that configures the routes that can be accessed by the user.
	 *
	 * @param method the method used to access the route.
	 * @param routes the routes that can be accessed by the user.
	 *
	 * @return this router security config.
	 *
	 * @throws Exception if an error occurs.
	 */
	private RouterSecurityConfig registerRoutes(HttpMethod method, String... routes) throws Exception {
		this.http.authorizeRequests()
		         .antMatchers(method, routes)
		         .hasAnyAuthority(this.authorizedRoles);

		return this;
	}
}
