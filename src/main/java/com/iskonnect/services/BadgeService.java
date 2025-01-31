package com.iskonnect.services;

import com.iskonnect.models.Badge;

import java.sql.*;
import java.util.*;

public class BadgeService extends BaseService {

    public List<Badge> getUserBadges(String userId) {
        List<Badge> badges = new ArrayList<>();
        String query = """
            SELECT b.name, b.description, b.image_url, b.requirement_points 
            FROM badges b
            JOIN user_badges ub ON b.badge_id = ub.badge_id
            WHERE ub.user_id = ?
            ORDER BY b.requirement_points DESC
        """;

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setString(1, userId);

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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
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

        String highestBadge = null;
        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    highestBadge = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return highestBadge;
    }

    public List<Badge> getAvailableBadges(int currentPoints) {
        List<Badge> badges = new ArrayList<>();
        String query = """
            SELECT * FROM badges 
            WHERE requirement_points <= ?
            ORDER BY requirement_points ASC
        """;

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return badges;
    }

    public List<Badge> evaluateAndAwardBadges(String userId, int currentPoints) {
        List<Badge> newlyAwardedBadges = new ArrayList<>();

        String badgeQuery = """
            SELECT b.badge_id, b.name, b.description, b.image_url, b.requirement_points 
            FROM badges b
            WHERE b.requirement_points <= ?
            AND b.badge_id NOT IN (
                SELECT badge_id FROM user_badges WHERE user_id = ?
            )
        """;

        String awardQuery = "INSERT INTO user_badges (user_id, badge_id) VALUES (?, ?)";

        try {
            openConnection();
            beginTransaction();

            try (PreparedStatement badgeStmt = getConnection().prepareStatement(badgeQuery);
                 PreparedStatement awardStmt = getConnection().prepareStatement(awardQuery)) {

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
                commitTransaction();

            } catch (SQLException e) {
                rollbackTransaction();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
 closeConnection();
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

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    stats.put(rs.getString("name"), rs.getInt("awarded_count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return stats;
    }
}