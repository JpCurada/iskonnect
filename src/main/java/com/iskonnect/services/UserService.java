package com.iskonnect.services;

import com.iskonnect.models.*;
import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;

public class UserService {
    public User authenticate(String email, String passwordHash) {
        String query = """
            SELECT u.*, uc.email 
            FROM users u 
            JOIN user_credentials uc ON u.user_id = uc.user_id 
            WHERE uc.email = ? AND uc.password_hash = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String userType = rs.getString("user_type");
                if (userType.equals("STUDENT")) {
                    return new Student(
                        rs.getString("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                    );
                } else {
                    return new Admin(
                        rs.getString("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(User user, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insert credentials
            String credQuery = "INSERT INTO user_credentials (user_id, email, password_hash) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(credQuery)) {
                stmt.setString(1, user.getUserId());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, password);
                stmt.executeUpdate();
            }
            
            // Insert user details
            String userQuery = "INSERT INTO users (user_id, first_name, last_name, user_type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                stmt.setString(1, user.getUserId());
                stmt.setString(2, user.getFirstName());
                stmt.setString(3, user.getLastName());
                stmt.setString(4, user.getUserType());
                stmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}