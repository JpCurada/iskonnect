// Path: src/main/java/com/iskonnect/services/AdminUserService.java

package com.iskonnect.services;

import com.iskonnect.models.UserDetails;
import com.iskonnect.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class AdminUserService {
    
    public ObservableList<UserDetails> getAllUsers() throws SQLException {
        ObservableList<UserDetails> users = FXCollections.observableArrayList();
        String query = """
            SELECT 
                u.user_id,
                u.first_name,
                u.last_name,
                u.points,
                uc.email,
                COUNT(DISTINCT r.report_id) as report_count,
                COUNT(DISTINCT CASE WHEN v.vote_type = 'UPVOTE' THEN v.vote_id END) as upvote_count
            FROM users u
            LEFT JOIN user_credentials uc ON u.user_id = uc.user_id
            LEFT JOIN reports r ON u.user_id = r.reporter_id
            LEFT JOIN votes v ON u.user_id = v.user_id
            GROUP BY u.user_id, u.first_name, u.last_name, u.points, uc.email
            ORDER BY u.points DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new UserDetails(
                    rs.getString("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("points"),
                    rs.getString("email"),
                    rs.getInt("report_count"),
                    rs.getInt("upvote_count")
                ));
            }
        }
        return users;
    }

    public boolean deleteUser(String userId) throws SQLException {
        // Order of deletion is important due to foreign key constraints
        String[] queries = {
            "DELETE FROM votes WHERE user_id = ?",              // Delete user's votes
            "DELETE FROM votes WHERE material_id IN (SELECT material_id FROM materials WHERE uploader_id = ?)",  // Delete votes on user's materials
            "DELETE FROM reports WHERE reporter_id = ?",        // Delete reports made by user
            "DELETE FROM reports WHERE material_id IN (SELECT material_id FROM materials WHERE uploader_id = ?)", // Delete reports on user's materials
            "DELETE FROM user_badges WHERE user_id = ?",        // Delete user's badges
            "DELETE FROM materials WHERE uploader_id = ?",      // Delete user's materials
            "DELETE FROM users WHERE user_id = ?",             // Delete the user first
            "DELETE FROM user_credentials WHERE user_id = ?"   // Then delete credentials
        };

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Execute each delete query in order
                for (String query : queries) {
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, userId);
                        stmt.executeUpdate();
                    }
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}