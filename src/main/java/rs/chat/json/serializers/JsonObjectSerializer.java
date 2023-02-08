package rs.chat.json.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class JsonObjectSerializer extends JsonSerializer<JsonObject> {
	@Override
	public void serialize(JsonObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		traverseJsonObject(value, gen);
		gen.writeEndObject();
	}

	private void traverseJsonObject(JsonObject object, JsonGenerator gen) throws IOException {
		for (var entry : object.entrySet()) {
			gen.writeFieldName(entry.getKey());
			traverseElement(gen, entry.getValue());
		}
	}

	private void traverseJsonArray(JsonArray array, JsonGenerator gen) throws IOException {
		for (JsonElement element : array) {
			traverseElement(gen, element);
		}
	}

	private void traverseElement(JsonGenerator gen, JsonElement element) throws IOException {
		if (element.isJsonPrimitive()) {
			gen.writeObject(element.getAsJsonPrimitive());
		} else if (element.isJsonObject()) {
			gen.writeStartObject();
			traverseJsonObject(element.getAsJsonObject(), gen);
			gen.writeEndObject();
		} else if (element.isJsonArray()) {
			gen.writeStartArray();
			traverseJsonArray(element.getAsJsonArray(), gen);
			gen.writeEndArray();
		} else if (element.isJsonNull()) {
			gen.writeNull();
		}
	}
}
