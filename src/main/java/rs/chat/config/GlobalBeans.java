package rs.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Clock;

@Configuration
public class GlobalBeans {
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	/**
	 * Password encoder bean.
	 *
	 * @return {@link BCryptPasswordEncoder} bean.
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
