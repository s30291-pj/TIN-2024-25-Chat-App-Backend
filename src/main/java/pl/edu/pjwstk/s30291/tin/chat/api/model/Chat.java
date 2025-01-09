package pl.edu.pjwstk.s30291.tin.chat.api.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Chat {
	private List<ChatMessage> history = new ArrayList<>();
	
	@Getter private String identifier;	
	
	public Chat() {}
	
	public Chat(String identifier) {
		this.identifier = identifier;
	}
	
	public void AddMessage(ChatMessage message) {
		history.add(message);
	}
}
