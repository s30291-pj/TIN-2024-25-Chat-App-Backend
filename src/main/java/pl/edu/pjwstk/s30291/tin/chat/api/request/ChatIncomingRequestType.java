package pl.edu.pjwstk.s30291.tin.chat.api.request;

import com.google.gson.JsonObject;
import com.surrealdb.driver.SyncSurrealDriver;

import lombok.Getter;
import pl.edu.pjwstk.s30291.tin.chat.api.model.Chat;
import pl.edu.pjwstk.s30291.tin.chat.api.model.ChatAccount;
import pl.edu.pjwstk.s30291.tin.chat.api.model.ChatContact;
import pl.edu.pjwstk.s30291.tin.chat.api.model.ChatMessage;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatAuthEstablishedRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatAuthRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatContactEstablishedRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatContactInviteRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatMessageSentRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.JsonUtility;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.SurrealDatabase;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.TriConsumer;
import pl.edu.pjwstk.s30291.tin.chat.api.websocket.SessionDetails;
import pl.edu.pjwstk.s30291.tin.chat.api.websocket.SessionWebsocket;

public enum ChatIncomingRequestType {
	CHAT_AUTH(ChatAuthRequest.class, (ws, session, req) -> {
		session.auth(req.getUsername(), req.getPassphrase());
		
		ws.message(session.getSession(), new ChatAuthEstablishedRequest(session.getUuid()));
	}),
	
	CHAT_CONTACT_INVITE(ChatContactInviteRequest.class,(ws, session, req) -> {
		try {
			String senderId = session.getHash();
			String receiverId = req.getIdentifier();
			
			ChatAccount senderAccount = session.getAccountDetails();
			
			// MUTUAL CONTACT
			if(senderAccount.containsInvitation(receiverId)) {
				ChatAccount receiverAccount = SurrealDatabase.selectOne("account", receiverId, ChatAccount.class);
				
				senderAccount.removeInvitation(receiverId);		
				
				ChatContact contactForSender = new ChatContact(receiverAccount.getUsername(), receiverId);
				ChatContact contactForReceiver = new ChatContact(senderAccount.getUsername(), senderId);
				
				senderAccount.addContact(contactForSender);
				receiverAccount.addContact(contactForReceiver);
				
				SurrealDatabase.updateOne("account", senderId, senderAccount);
				SurrealDatabase.updateOne("account", receiverId, receiverAccount);
				
				ws.message(senderId, new ChatContactEstablishedRequest(contactForSender.getUsername(), contactForSender.getHash()));
				ws.message(receiverId, new ChatContactEstablishedRequest(contactForReceiver.getUsername(), contactForReceiver.getHash()));
				return;
			}
			
			// ONE WAY CONTACT
			ChatAccount receiverAccount = SurrealDatabase.selectOne("account", receiverId, ChatAccount.class);
			
			if(receiverAccount != null) {
				receiverAccount.addInvitation(senderId);
				SurrealDatabase.updateOne("account", receiverId, receiverAccount);
			}
			else {
				System.out.println("Account %s does not exist!".formatted(receiverId));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}),
	
	CHAT_MESSAGE_SENT(ChatMessageSentRequest.class,(ws, session, req) -> {
		try {
			SyncSurrealDriver driver = SurrealDatabase.getDriver();
			
			ChatMessage msg = new ChatMessage(req.getSender(), req.getContent(), req.getTimestamp());
			
			if(!session.isAuthorized()) {
				System.out.println("Tried to send request not authorized!");
				return;
			}
			
			String chatId = Chat.getChatIdentifier(session.getHash(), req.getReceiver());
			String json = JsonUtility.toJson(msg);
			
			driver.query("UPDATE chats:%s SET history += '%s'".formatted(chatId, json), null, JsonObject.class);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}),
	;
	
	@Getter private String name;
	@Getter private Class<? extends ChatRequest> clazz;
	@Getter private TriConsumer<SessionWebsocket, SessionDetails, ChatRequest> handler;
	
	private <R extends ChatRequest> ChatIncomingRequestType(Class<R> clazz, TriConsumer<SessionWebsocket, SessionDetails, R> handler) {
		this.name =  ChatRequest.getRequestName(clazz);
		this.clazz = clazz;
		this.handler = (TriConsumer<SessionWebsocket, SessionDetails, ChatRequest>) handler;
	}
	
	public void handle(String json, SessionDetails details, SessionWebsocket websocket) {
		handler.accept(websocket, details, (ChatRequest) JsonUtility.fromJson(clazz, json));
	}
	
	public static ChatIncomingRequestType getRequestType(ChatRequest request) {
		return getRequestType(request.getName());
	}
	
	public static ChatIncomingRequestType getRequestType(String name) {
		for(ChatIncomingRequestType req : values()) {
			if(req.name.equals(name)) return req;
		}
		
		return null;
	}
}
