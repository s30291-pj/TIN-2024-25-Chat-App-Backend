package pl.edu.pjwstk.s30291.tin.chat.api.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

@Getter 
public class ChatAccount {
	private Set<String> invitations = new HashSet<>();
	private List<ChatContact> contacts = new ArrayList<>();
	private String username;
	
	public ChatAccount(String username) {
		this.username = username;
	}
	
	public boolean containsInvitation(String hash) {
		return invitations.contains(hash);
	}
	
	public boolean containsContact(String hash) {
		return contacts.stream().filter((c) -> c.getIdentifier().equals(hash)).toList().size() > 0;
	}
	
	public void addInvitation(String hash) {
		invitations.add(hash);
	}
	
	public void removeInvitation(String hash) {
		invitations.remove(hash);
	}
	
	public void addContact(ChatContact contact) {
		contacts.add(contact);
	}
}
