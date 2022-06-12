package ule.chat.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public final class Utils {
	private Utils() {
	}

	public static <K, V> Type getTypeTokenFromClasses(Class<K> keyClass, Class<V> valueClass) {
		return new TypeToken<Map<K, V>>() {
		}.getType();
	}
}
