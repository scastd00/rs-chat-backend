package ule.chat.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public final class Utils {
	private Utils() {
	}

	public static <K, V> V deserialize(String json, Class<K> keyClass,
	                                   Class<V> valueClass, K key) {
		Map<K, V> storage = Constants.gson.fromJson(json, new TypeToken<Map<K, V>>() {
		}.getType());
		return storage.get(key);
	}

	public static <K, V> V deserialize(String json, Type type, K key) {
		Map<K, V> tempStorage = Constants.gson.fromJson(json, type);
		return tempStorage.get(key);
	}

	public static <K, V> Type getTypeTokenFromClasses(Class<K> keyClass, Class<V> valueClass) {
		return new TypeToken<Map<K, V>>() {
		}.getType();
	}
}
