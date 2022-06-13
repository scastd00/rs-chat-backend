package ule.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// https://youtu.be/VVn9OG9nfH0?t=2983

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//! IMPORTANT NOTE: antMatchers() is for specifying the path that do not
		//! care about which HttpMethod is used.

		http.authorizeRequests()
		    .antMatchers("/").permitAll() // Any user
		    .antMatchers("/login").permitAll()
//		    .anyRequest().authenticated()
		    .and().httpBasic() // Authenticate with username and password.
		    //For REST services disable CSRF protection.
		    //See https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html#when-to-use-csrf-protection
		    .and().cors()
		    .and().csrf().disable();
	}
}
