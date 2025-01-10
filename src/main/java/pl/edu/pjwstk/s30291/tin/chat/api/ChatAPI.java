package pl.edu.pjwstk.s30291.tin.chat.api;

import static spark.Spark.post;
import static spark.Spark.webSocket;

import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

import pl.edu.pjwstk.s30291.tin.chat.api.model.Chat;
import pl.edu.pjwstk.s30291.tin.chat.api.model.ChatAccount;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatContactsRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatHistoryRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.JsonUtility;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.SurrealDatabase;
import pl.edu.pjwstk.s30291.tin.chat.api.websocket.SessionWebsocket;
import spark.Spark;

public class ChatAPI {
	/*
	 * Program parameters: chatPort, surrealAdress, surrealUsername, surrealPassword,
	 */
	public static void main(String... args) {
		SurrealDatabase.connect(args[1], args[2], args[3]);
		
		Spark.port(Integer.valueOf(args[0]));
		
		webSocket("/api/session", new SessionWebsocket());
		
		post("/api/contacts", (req, res) -> {
			String json = req.body();
			
			ChatContactsRequest request = JsonUtility.fromJson(ChatContactsRequest.class, json);
			String hash = hash(request.getUsername(), request.getPassphrase());
			
			ChatAccount account = SurrealDatabase.selectOne("account", hash, ChatAccount.class);
			
			return JsonUtility.toJson((account != null) ? account.getContacts() : new ArrayList<>());
		});
		
		post("/api/chat", (req, res) -> {
			String json = req.body();
			
			ChatHistoryRequest request = JsonUtility.fromJson(ChatHistoryRequest.class, json);
			String hash = hash(request.getUsername(), request.getPassphrase());
			
			String identifier = Chat.getChatIdentifier(hash, request.getReceiver());
			
			Chat chat = SurrealDatabase.selectOne("chat", identifier, Chat.class);
			
			return JsonUtility.toJson((chat != null) ? chat.getHistory() : new ArrayList<>());
		});
	}
	
	private static String hash(String username, String passphrase) {
		return DigestUtils.sha256Hex(username + passphrase);
	}
	
}
