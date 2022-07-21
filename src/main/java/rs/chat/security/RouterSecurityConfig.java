package rs.chat.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static rs.chat.utils.Constants.STRING_ARRAY;

public class RouterSecurityConfig {
	private final HttpSecurity http;
	private final String[] authorizedRoles;
	private final List<String> getRoutes = new ArrayList<>();
	private final List<String> postRoutes = new ArrayList<>();
	private final List<String> putRoutes = new ArrayList<>();
	private final List<String> deleteRoutes = new ArrayList<>();

	public RouterSecurityConfig(HttpSecurity http, String[] authorizedRoles) {
		this.http = http;
		this.authorizedRoles = authorizedRoles;
	}

	public RouterSecurityConfig addGETRoutes(String... urls) {
		Collections.addAll(this.getRoutes, urls);
		return this;
	}

	public RouterSecurityConfig addPOSTRoutes(String... urls) {
		Collections.addAll(this.postRoutes, urls);
		return this;
	}

	public RouterSecurityConfig addPUTRoutes(String... urls) {
		Collections.addAll(this.putRoutes, urls);
		return this;
	}

	public RouterSecurityConfig addDELETERoutes(String... urls) {
		Collections.addAll(this.deleteRoutes, urls);
		return this;
	}

	public void registerRoutes() throws Exception {
		this.http.authorizeRequests()
		         .antMatchers(GET, this.getRoutes.toArray(STRING_ARRAY))
		         .hasAnyAuthority(this.authorizedRoles);

		this.http.authorizeRequests()
		         .antMatchers(POST, this.postRoutes.toArray(STRING_ARRAY))
		         .hasAnyAuthority(this.authorizedRoles);

		this.http.authorizeRequests()
		         .antMatchers(PUT, this.putRoutes.toArray(STRING_ARRAY))
		         .hasAnyAuthority(this.authorizedRoles);

		this.http.authorizeRequests()
		         .antMatchers(DELETE, this.deleteRoutes.toArray(STRING_ARRAY))
		         .hasAnyAuthority(this.authorizedRoles);
	}
}
