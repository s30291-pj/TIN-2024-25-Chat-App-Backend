package pl.edu.pjwstk.s30291.tin.chat.api.websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

import pl.edu.pjwstk.s30291.tin.chat.api.request.ChatIncomingRequestType;
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
    	System.out.println("Connected!" + session.getRemoteAddress().toString());
    	
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
    	System.out.println("Disconnected!" + session.getRemoteAddress().toString());
    	
    	UUID id = sessionsIdentifiers.get(session);
    	SessionDetails details = sessionsDetails.get(id);
    	
    	sessionsDetails.remove(id);
    	sessionsIdentifiers.remove(session);
    	
    	if(details.getHash() != null) {
    		sessionsHashes.remove(details.getHash());
    	}
    }
    

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
    	System.out.println("Received from " + session.getRemoteAddress() + ": " + message);
    	
    	ChatRequest request = JsonUtility.fromJson(ChatRequest.class, message);

    	ChatIncomingRequestType type = ChatIncomingRequestType.getRequestType(request.getName());
    	
    	type.handle(message, getSessionDetails(session), this);
    }
    
    public void message(UUID uuid, ChatRequest request) {
    	message(getSessionDetails(uuid).getSession(), request);
    }
    
    public void message(String hash, ChatRequest request) {
    	getSessionsDetails(hash).forEach((details) -> message(details.getSession(), request));
    }
    
    public void message(Session session, ChatRequest request) {
    	try { 
    		String json = request.toJson();
    		session.getRemote().sendString(json); 
    		System.out.println("Sent to " + session.getRemoteAddress() + ": " + json);
    	} catch (Exception e) { e.printStackTrace(); }
    }
    
    public void addIdentifiedSession(String hash, UUID uuid) {
    	sessionsHashes.put(hash, uuid);
    }
    
    public List<SessionDetails> getSessionsDetails(String hash) {
    	Collection<UUID> uuids = sessionsHashes.get(hash);
    	
    	return uuids.stream().map((uuid) -> sessionsDetails.get(uuid)).toList();
    }
    
    public SessionDetails getSessionDetails(Session session) {
    	return getSessionDetails(sessionsIdentifiers.get(session));
    }
    
    public SessionDetails getSessionDetails(UUID uuid) {
    	return sessionsDetails.get(uuid);
    }
}
