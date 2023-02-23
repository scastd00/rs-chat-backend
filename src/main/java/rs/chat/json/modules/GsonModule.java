package rs.chat.json.modules;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import rs.chat.json.serializers.JsonArraySerializer;
import rs.chat.json.serializers.JsonObjectSerializer;
import rs.chat.json.serializers.JsonPrimitiveSerializer;

/**
 * Jackson module to serialize GSON objects.
 */
public class GsonModule extends SimpleModule {
	public GsonModule() {
		super("GsonModule");

		addSerializer(JsonObject.class, new JsonObjectSerializer());
		addSerializer(JsonPrimitive.class, new JsonPrimitiveSerializer());
		addSerializer(JsonArray.class, new JsonArraySerializer());
	}
}
