package rs.chat.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Password encoder configuration bean.
 */
@Configuration
public class PasswordEncoderConfig {
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
