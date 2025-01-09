package pl.edu.pjwstk.s30291.tin.chat.api.utility;

import com.google.gson.Gson;

public class JsonUtility {
	private static Gson gson = new Gson();
	
	public static String toJson(Object object) {
		return gson.toJson(object);
	}
	
	public static <T> T fromJson(Class<T> clazz, String json) {
		return gson.fromJson(json, clazz);
	}
}
