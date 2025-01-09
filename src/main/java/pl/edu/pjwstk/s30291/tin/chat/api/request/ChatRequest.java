package pl.edu.pjwstk.s30291.tin.chat.api.request;

import lombok.Getter;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.JsonUtility;

public class ChatRequest {
	public static <R extends ChatRequest> String getRequestName(R request) {
		return getRequestName(request.getClass());
	}
	
	public static <R extends ChatRequest> String getRequestName(Class<R> clazz) {
		StringBuilder builder = new StringBuilder();
		
		char[] array = clazz.getSimpleName().replaceAll("Request", "").toCharArray();
		
		for(int i = 0; i < array.length; i++) {
			char c = array[i];
			
			if(i > 0 && Character.isUpperCase(c)) {
				builder.append('_');
			}
			
			c = Character.toLowerCase(c);
			
			builder.append(c);
		}
		
		return builder.toString();
	}
	
	@Getter
	private String name = getRequestName(this);
	
	public String toJson() {
		return JsonUtility.toJson(this);
	}
}
