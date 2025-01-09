package pl.edu.pjwstk.s30291.tin.chat.api.request.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.edu.pjwstk.s30291.tin.chat.api.request.ChatRequest;

@AllArgsConstructor
@Getter
public class ChatContactEstablishedRequest extends ChatRequest {
	private String username;
	private String identifier;
	
}
