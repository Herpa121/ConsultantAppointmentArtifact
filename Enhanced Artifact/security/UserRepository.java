package security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import security.User.UserRole;

public class UserRepository {
	private final Connection connection;

	// Constructor
	public UserRepository(Connection connection) {
		this.connection = connection;
	}
	
	// Get user from database
	public User getUserByUsername(String username) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new User(rs.getString("id"), rs.getString("username"), rs.getString("passwordHash"),
						UserRole.valueOf(rs.getString("role")));
			}
		}
		return null;
	}
	// Save user to database
	public void saveUser(User user) throws SQLException {
		String query = "INSERT INTO users (id, username, passwordHash, role) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, user.getId());
			stmt.setString(2, user.getUsername());
			stmt.setString(3, user.getPasswordHash());
			stmt.setString(4, user.getRole().name());
			stmt.executeUpdate();
		}
	}
}
