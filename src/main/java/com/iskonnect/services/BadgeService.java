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

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    badges.add(new Badge(
                        rs.getString("name"),
                        rs.getString("description"),
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
        String badgeQuery = """
            SELECT badge_id, name, description, requirement_points FROM badges
            WHERE requirement_points <= ?
            AND badge_id NOT IN (
                SELECT badge_id FROM user_badges WHERE user_id = ?
            )
        """;
        String awardQuery = "INSERT INTO user_badges (user_id, badge_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                PreparedStatement badgeStmt = conn.prepareStatement(badgeQuery);
                PreparedStatement awardStmt = conn.prepareStatement(awardQuery)
            ) {
                badgeStmt.setInt(1, currentPoints);
                badgeStmt.setString(2, userId);

                try (ResultSet rs = badgeStmt.executeQuery()) {
                    while (rs.next()) {
                        int badgeId = rs.getInt("badge_id");
                        awardStmt.setString(1, userId);
                        awardStmt.setInt(2, badgeId);
                        awardStmt.addBatch();
                        newlyAwardedBadges.add(new Badge(
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("requirement_points")
                        ));
                    }
                }

                // Execute the batch insert for awarded badges
                awardStmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback(); // Rollback in case of an error
                throw e; // Rethrow the exception for further handling
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newlyAwardedBadges;
    }
}