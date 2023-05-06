package rs.chat.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rs.chat.json.modules.GsonModule;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class JsonConstants {
	static final ObjectMapper JACKSON = new ObjectMapper()
			.setSerializationInclusion(JsonInclude.Include.NON_NULL) // Don't serialize null values
			.registerModules(new JavaTimeModule(), new GsonModule());

	static final Gson GSON = Converters.registerAll(new GsonBuilder()).create();
}
