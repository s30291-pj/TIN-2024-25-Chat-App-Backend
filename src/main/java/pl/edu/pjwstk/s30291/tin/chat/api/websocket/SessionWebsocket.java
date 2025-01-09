package pl.edu.pjwstk.s30291.tin.chat.api.websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import pl.edu.pjwstk.s30291.tin.chat.api.request.ChatRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.request.impl.ChatAuthEstablishedRequest;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.JsonUtility;

@WebSocket
public class SessionWebsocket {
	private static final Map<Session, UUID> sessionsIdentifiers = new ConcurrentHashMap<>();
	private static final Map<UUID, SessionDetails> sessionsDetails = new ConcurrentHashMap<>();
	private static final MultiValuedMap<String, UUID> sessionsHashes = new ArrayListValuedHashMap<>();
	
    @OnWebSocketConnect
    public void onConnected(Session session) {
    	SessionDetails details = new SessionDetails(session) {
			@Override
			public void onAuth(String hash) {
				message(hash, new ChatAuthEstablishedRequest(this.getUuid()));
			}
    	};
    	
    	sessionsDetails.put(details.getUuid(), details);
    	sessionsIdentifiers.put(session, details.getUuid());
    }

    @OnWebSocketClose
    public void onClosed(Session session, int statusCode, String reason) {
    	UUID id = sessionsIdentifiers.get(session);
    	SessionDetails details = sessionsDetails.get(id);
    	
    	sessionsDetails.remove(id);
    	sessionsIdentifiers.remove(session);
    	sessionsHashes.remove(id);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
    	JsonUtility.fromJson(ChatRequest.class, message);
    	
        System.out.println("Got: " + message);
        session.getRemote().sendString(message);
    }
    
    public void message(UUID uuid, ChatRequest request) {
    	message(sessionsDetails.get(uuid).getSession(), request);
    }
    
    public void message(String hash, ChatRequest request) {
    	Collection<UUID> uuids = sessionsHashes.get(hash);
    	
    	uuids.forEach((uuid) ->  message(sessionsDetails.get(uuid).getSession(), request));
    }
    
    public void message(Session session, ChatRequest request) {
    	try { session.getRemote().sendString(request.toJson()); } 
    	catch (Exception e) { e.printStackTrace(); }
    }
}
