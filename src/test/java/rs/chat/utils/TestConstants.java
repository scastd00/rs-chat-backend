package rs.chat.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.datafaker.Faker;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import rs.chat.json.modules.GsonModule;

public class TestConstants {
	public static final RecursiveComparisonConfiguration
			TEST_COMPARISON_CONFIG = RecursiveComparisonConfiguration.builder()
			                                                         .withIgnoredFields("id")
			                                                         .build();
	public static final ObjectMapper TEST_OBJECT_MAPPER = new ObjectMapper()
			.setSerializationInclusion(JsonInclude.Include.NON_NULL) // Don't serialize null values
			.registerModules(new JavaTimeModule(), new GsonModule());

	public static final Gson TEST_GSON = Converters.registerAll(new GsonBuilder()).create();

	public static final Faker FAKER = new Faker();
	public static final String TEST_PASSWORD = "!PasswordSpecialChars_123$";
}
