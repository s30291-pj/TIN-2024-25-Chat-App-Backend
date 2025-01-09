package pl.edu.pjwstk.s30291.tin.chat.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatMessage {
	private String sender;
	private String content;
	private long timestamp;
}
