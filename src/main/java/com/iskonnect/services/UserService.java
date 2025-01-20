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

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    if ("STUDENT".equals(userType)) {
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
            }

            // Clean up prepared statements
            try (PreparedStatement deallocateStmt = conn.prepareStatement("DEALLOCATE ALL")) {
                deallocateStmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(User user, String password) {
        String credQuery = "INSERT INTO user_credentials (user_id, email, password_hash) VALUES (?, ?, ?)";
        String userQuery = "INSERT INTO users (user_id, first_name, last_name, user_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                PreparedStatement credStmt = conn.prepareStatement(credQuery);
                PreparedStatement userStmt = conn.prepareStatement(userQuery)
            ) {
                credStmt.setString(1, user.getUserId());
                credStmt.setString(2, user.getEmail());
                credStmt.setString(3, password);
                credStmt.executeUpdate();

                userStmt.setString(1, user.getUserId());
                userStmt.setString(2, user.getFirstName());
                userStmt.setString(3, user.getLastName());
                userStmt.setString(4, user.getUserType());
                userStmt.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                try (PreparedStatement deallocateStmt = conn.prepareStatement("DEALLOCATE ALL")) {
                    deallocateStmt.execute();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
