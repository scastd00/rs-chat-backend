package rs.chat.json.modules;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import rs.chat.json.serializers.JsonObjectSerializer;
import rs.chat.json.serializers.JsonPrimitiveSerializer;

public class GsonModule extends SimpleModule {
	public GsonModule() {
		super("GsonModule");

		addSerializer(JsonObject.class, new JsonObjectSerializer());
		addSerializer(JsonPrimitive.class, new JsonPrimitiveSerializer());
	}
}
