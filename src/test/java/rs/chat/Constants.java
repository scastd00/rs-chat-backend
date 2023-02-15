package rs.chat;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import rs.chat.config.security.JWTService;

import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;

public class Constants {
	public static final RecursiveComparisonConfiguration
			TEST_COMPARISON_CONFIG = RecursiveComparisonConfiguration.builder()
			                                                         .withIgnoredFields("id")
			                                                         .build();
	public static final String TEST_TOKEN = JWT_TOKEN_PREFIX + JWTService.generateTmpToken("test", "test");
}
