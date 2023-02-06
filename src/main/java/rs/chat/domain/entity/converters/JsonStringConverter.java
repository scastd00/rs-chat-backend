package rs.chat.domain.entity.converters;

import com.google.gson.JsonObject;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rs.chat.utils.Utils;

@Converter
public class JsonStringConverter implements AttributeConverter<JsonObject, String> {
	@Override
	public String convertToDatabaseColumn(JsonObject attribute) {
		return attribute.toString();
	}

	@Override
	public JsonObject convertToEntityAttribute(String dbData) {
		return Utils.parseJson(dbData);
	}
}
