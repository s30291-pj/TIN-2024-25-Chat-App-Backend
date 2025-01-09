package pl.edu.pjwstk.s30291.tin.chat.api;

import static spark.Spark.post;
import static spark.Spark.stop;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatAuthRequest;
import spark.Spark;

public class ChatAPI {

	public static void main(String... args) {
		post("/api/contacts", (req, res) -> {
			return res;
		});
		
		post("/api/chat", (req, res) -> {
			return res;
		});
		
		Spark.webSocket("/api/session", new WebSocketHandler() {

			@Override
			public void configure(WebSocketServletFactory factory) {
			}
	
			
		});
		
		stop();
	}
	
}
