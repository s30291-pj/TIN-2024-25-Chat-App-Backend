package pl.edu.pjwstk.s30291.tin.chat.api.websocket;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.websocket.api.Session;

import lombok.Getter;
import pl.edu.pjwstk.s30291.tin.chat.api.model.ChatAccount;
import pl.edu.pjwstk.s30291.tin.chat.api.utility.SurrealDatabase;

public abstract class SessionDetails {
	@Getter private UUID uuid = UUID.randomUUID();
	@Getter private Session session;
	@Getter private String hash;
	@Getter private String username;

	public SessionDetails(Session session) {
		this.session = session;
	}
	
	public void auth(String username, String passphrase) {
		this.hash = DigestUtils.sha256Hex(username + passphrase);
		this.username = username;
		
		ChatAccount account = getAccountDetails(); // maybe send it?
		
		onAuth(hash);
	}
	
	public ChatAccount getAccountDetails() {
		if(hash == null) {
			System.out.println("Unauthorized!");
			return null; 
		}
		
		ChatAccount account = SurrealDatabase.selectOne("account", hash, ChatAccount.class);
		
		if(account == null) {
			account = new ChatAccount(username);
			SurrealDatabase.createOne("account", hash, account);
		}
		
		return account;
	}
	
	public abstract void onAuth(String hash);
	
	public boolean isAuthorized() {
		return (hash != null);
	}
	
}
