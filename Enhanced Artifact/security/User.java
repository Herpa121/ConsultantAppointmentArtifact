package security;

public class User {
	private String id;
	private String username;
	private String passwordHash;
	private UserRole role;
	public enum UserRole {
		CONSULTANT, CLIENT;
	}

	// Constructor
	public User(String id, String username, String passwordHash, UserRole role) {
		this.id = id;
		this.username = username;
		this.passwordHash = passwordHash;
		this.role = role;
	}

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}
