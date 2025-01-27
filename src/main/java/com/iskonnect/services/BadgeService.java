package com.iskonnect.services;

import com.iskonnect.models.Badge;
import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class BadgeService {

    public List<Badge> getUserBadges(String userId) {
        List<Badge> badges = new ArrayList<>();
        String query = """
            SELECT b.name, b.description, b.image_url, b.requirement_points 
            FROM badges b
            JOIN user_badges ub ON b.badge_id = ub.badge_id
            WHERE ub.user_id = ?
            ORDER BY b.requirement_points DESC
        """;
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, userId);
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    badges.add(new Badge(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image_url"), // Now correctly referencing image_url from badges
                        rs.getInt("requirement_points")
                    ));
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public String getHighestBadge(String userId) {
        String query = """
            SELECT b.name 
            FROM badges b
            JOIN user_badges ub ON b.badge_id = ub.badge_id
            WHERE ub.user_id = ?
            ORDER BY b.requirement_points DESC
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Badge> getAvailableBadges(int currentPoints) {
        List<Badge> badges = new ArrayList<>();
        String query = """
            SELECT * FROM badges 
            WHERE requirement_points <= ?
            ORDER BY requirement_points ASC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentPoints);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    badges.add(new Badge(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getInt("requirement_points")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public List<Badge> evaluateAndAwardBadges(String userId, int currentPoints) {
        List<Badge> newlyAwardedBadges = new ArrayList<>();
        
        // Define the badge query to select eligible badges
        String badgeQuery = """
            SELECT b.badge_id, b.name, b.description, b.image_url, b.requirement_points 
            FROM badges b
            WHERE b.requirement_points <= ?
            AND b.badge_id NOT IN (
                SELECT badge_id FROM user_badges WHERE user_id = ?
            )
        """;
    
        // Define the award query to insert a new badge for the user
        String awardQuery = "INSERT INTO user_badges (user_id, badge_id) VALUES (?, ?)";
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
    
            try (PreparedStatement badgeStmt = conn.prepareStatement(badgeQuery);
                 PreparedStatement awardStmt = conn.prepareStatement(awardQuery)) {
                
                badgeStmt.setInt(1, currentPoints);
                badgeStmt.setString(2, userId);
    
                ResultSet rs = badgeStmt.executeQuery();
                while (rs.next()) {
                    int badgeId = rs.getInt("badge_id");
                    
                    // Award the badge
                    awardStmt.setString(1, userId);
                    awardStmt.setInt(2, badgeId);
                    awardStmt.addBatch();
                    
                    // Add to newly awarded list
                    newlyAwardedBadges.add(new Badge(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getInt("requirement_points")
                    ));
                }
    
                awardStmt.executeBatch();
                conn.commit();
    
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newlyAwardedBadges;
    }

    public Map<String, Integer> getBadgeStats() {
        Map<String, Integer> stats = new HashMap<>();
        String query = """
            SELECT b.name, COUNT(ub.user_id) as awarded_count
            FROM badges b
            LEFT JOIN user_badges ub ON b.badge_id = ub.badge_id
            GROUP BY b.badge_id, b.name
            ORDER BY b.requirement_points DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("name"), rs.getInt("awarded_count"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}