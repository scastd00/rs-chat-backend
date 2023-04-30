package rs.chat.domain.entity.converters;

import com.google.gson.JsonObject;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rs.chat.json.JsonParser;

@Converter(autoApply = true)
public class JsonStringConverter implements AttributeConverter<JsonObject, String> {
	@Override
	public String convertToDatabaseColumn(JsonObject attribute) {
		return attribute.toString();
	}

	@Override
	public JsonObject convertToEntityAttribute(String dbData) {
		return JsonParser.parseJson(dbData);
	}
}
