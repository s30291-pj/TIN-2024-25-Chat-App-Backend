package pl.edu.pjwstk.s30291.tin.chat.api.request;

import java.util.HashMap;
import java.util.Map;

import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatAuthRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatContactInviteRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatMessageSentRequest;

public enum ChatIncomingRequestType {
	CHAT_AUTH(ChatAuthRequest.class),
	CHAT_CONTACT_INVITE(ChatContactInviteRequest.class),
	CHAT_MESSAGE_SENT(ChatMessageSentRequest.class),
	;
	
	private static Map<String, Class<? extends ChatRequest>> requests = new HashMap<>();
	
	private ChatIncomingRequestType(Class<? extends ChatRequest> clazz) {
		requests.put(ChatRequest.getRequestName(clazz), clazz);
	}
	
	public static Class<? extends ChatRequest> getRequestClass(ChatRequest request) {
		return getRequestClass(request.getName());
	}
	
	public static Class<? extends ChatRequest> getRequestClass(String name) {
		return requests.get(name);
	}
}
