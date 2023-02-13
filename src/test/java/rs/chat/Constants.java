package rs.chat;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

public class Constants {
	public static final RecursiveComparisonConfiguration
			TEST_COMPARISON_CONFIG = RecursiveComparisonConfiguration.builder()
			                                                         .withIgnoredFields("id")
			                                                         .build();
}
