package rs.chat.config.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import rs.chat.router.Routes;

import static rs.chat.utils.Constants.ACCEPTED_ORIGINS;

/**
 * Class that configures the CORS for the application.
 */
@Configuration
public class CorsConfig {
	/**
	 * Method that configures the CORS for the application.
	 *
	 * @return {@link WebMvcConfigurer} with the CORS configuration.
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NotNull CorsRegistry registry) {
				registry.addMapping(Routes.ALL_ROUTES)
				        .allowedMethods(CorsConfiguration.ALL)
				        .allowedOrigins(ACCEPTED_ORIGINS);
			}
		};
	}
}
