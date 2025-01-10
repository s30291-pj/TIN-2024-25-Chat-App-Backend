package pl.edu.pjwstk.s30291.tin.chat.api.request.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.edu.pjwstk.s30291.tin.chat.api.request.ChatRequest;

@AllArgsConstructor
@Getter
public class ChatHistoryRequest extends ChatRequest {
	private String username;
	private String passphrase;
	private String receiver;
}
