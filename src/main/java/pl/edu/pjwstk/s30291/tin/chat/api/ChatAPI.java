package pl.edu.pjwstk.s30291.tin.chat.api;

import static spark.Spark.post;
import static spark.Spark.stop;
import static spark.Spark.webSocket;

import pl.edu.pjwstk.s30291.tin.chat.api.model.Chat;
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
			return res;
		});
		
		post("/api/chat", (req, res) -> {
			return res;
		});
		
		stop();
	}
	
}
