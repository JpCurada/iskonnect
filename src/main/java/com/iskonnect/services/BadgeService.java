package com.iskonnect.services;

import com.iskonnect.models.Badge;
import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BadgeService {
    public List<Badge> getUserBadges(String userId) {
        List<Badge> badges = new ArrayList<>();
        String query = """
            SELECT b.* FROM badges b 
            JOIN user_badges ub ON b.badge_id = ub.badge_id 
            WHERE ub.user_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Badge badge = new Badge(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("requirement_points")
                );
                badges.add(badge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public void awardBadge(String userId, int badgeId) {
        String query = "INSERT INTO user_badges (user_id, badge_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            stmt.setInt(2, badgeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}