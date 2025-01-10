package pl.edu.pjwstk.s30291.tin.chat.api.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;

public class Chat {
	@Getter private List<ChatMessage> history = new ArrayList<>();
	
	@Getter private String identifier;	
	
	public Chat() {}
	
	public Chat(String identifier) {
		this.identifier = identifier;
	}
	
	public void AddMessage(ChatMessage message) {
		history.add(message);
	}
	
	public static String getChatIdentifier(String identifier1, String identifier2) {
		List<String> sorted = new ArrayList<>(List.of(identifier1, identifier2));
		
		sorted.sort(Comparator.naturalOrder());
		
		return sorted.get(0) + sorted.get(1);
	}
}
