package pl.edu.pjwstk.s30291.tin.chat.api.request.impl;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.edu.pjwstk.s30291.tin.chat.api.request.ChatRequest;

@AllArgsConstructor
@Getter
public class ChatAuthEstablishedRequest extends ChatRequest {
	private UUID session;
}
