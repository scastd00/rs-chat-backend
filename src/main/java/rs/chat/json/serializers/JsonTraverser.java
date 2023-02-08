package rs.chat.json.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonTraverser {
	static void traverseJsonObject(JsonObject object, JsonGenerator gen) throws IOException {
		for (var entry : object.entrySet()) {
			gen.writeFieldName(entry.getKey());
			traverseElement(entry.getValue(), gen);
		}
	}

	static void traverseJsonArray(JsonArray array, JsonGenerator gen) throws IOException {
		for (JsonElement element : array) {
			traverseElement(element, gen);
		}
	}

	static void traverseElement(JsonElement element, JsonGenerator gen) throws IOException {
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
