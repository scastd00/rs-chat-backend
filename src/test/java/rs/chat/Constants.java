package rs.chat;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

public class Constants {
	public static final RecursiveComparisonConfiguration
			RECURSIVE_COMPARISON_CONFIGURATION = RecursiveComparisonConfiguration.builder()
			                                                                     .withIgnoredFields("id")
			                                                                     .build();
}
