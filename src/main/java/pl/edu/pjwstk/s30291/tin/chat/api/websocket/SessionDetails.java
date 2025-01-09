package pl.edu.pjwstk.s30291.tin.chat.api.websocket;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.websocket.api.Session;

import lombok.Getter;

public abstract class SessionDetails {
	@Getter private UUID uuid = UUID.randomUUID();
	@Getter private Session session;
	@Getter private String hash;

	public SessionDetails(Session session) {
		this.session = session;
	}
	
	public void auth(String username, String passphrase) {
		this.hash = DigestUtils.sha256Hex(username + passphrase);
		
		onAuth(hash);
	}
	
	public abstract void onAuth(String hash);
	
}
