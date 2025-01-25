package com.iskonnect.services;

import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardService {
    
    public List<LeaderboardEntry> getLeaderboard(String timeFilter) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        String query = """
            SELECT 
                u.user_id,
                u.first_name,
                u.last_name,
                u.points,
                COUNT(DISTINCT m.material_id) as total_materials,
                COUNT(DISTINCT ub.badge_id) as total_badges
            FROM users u
            LEFT JOIN materials m ON u.user_id = m.uploader_id
            LEFT JOIN user_badges ub ON u.user_id = ub.user_id
            WHERE u.user_type = 'STUDENT'
            """ + 
            (timeFilter != null && !timeFilter.equals("All Time") ? 
             " AND m.upload_date " + getTimeFilterClause(timeFilter) : "") +
            """
            GROUP BY u.user_id, u.first_name, u.last_name, u.points
            ORDER BY u.points DESC, total_materials DESC
            LIMIT 50
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                    rs.getString("user_id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getInt("points"),
                    rs.getInt("total_materials"),
                    rs.getInt("total_badges")
                );
                leaderboard.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }

    private String getTimeFilterClause(String timeFilter) {
        return switch (timeFilter) {
            case "This Week" -> ">= CURRENT_DATE - INTERVAL '7 days'";
            case "This Month" -> ">= CURRENT_DATE - INTERVAL '30 days'";
            default -> "";
        };
    }

    public LeaderboardStats getStatistics() {
        LeaderboardStats stats = new LeaderboardStats();
        String query = """
            SELECT 
                (SELECT COUNT(DISTINCT user_id) 
                 FROM users 
                 WHERE user_type = 'STUDENT' AND 
                       user_id IN (SELECT DISTINCT uploader_id FROM materials)
                ) as contributors,
                (SELECT COUNT(*) FROM materials) as materials,
                (SELECT COALESCE(SUM(points), 0) 
                 FROM users 
                 WHERE user_type = 'STUDENT'
                ) as total_points
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.setTotalContributors(rs.getInt("contributors"));
                stats.setTotalMaterials(rs.getInt("materials"));
                stats.setTotalPoints(rs.getInt("total_points"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public static class LeaderboardEntry {
        private final String userId;
        private final String fullName;
        private final int points;
        private final int materials;
        private final int badges;
        private int rank;

        public LeaderboardEntry(String userId, String fullName, int points, int materials, int badges) {
            this.userId = userId;
            this.fullName = fullName;
            this.points = points;
            this.materials = materials;
            this.badges = badges;
        }

        // Getters and setters
        public String getUserId() { return userId; }
        public String getFullName() { return fullName; }
        public int getPoints() { return points; }
        public int getMaterials() { return materials; }
        public int getBadges() { return badges; }
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }

    public static class LeaderboardStats {
        private int totalContributors;
        private int totalMaterials;
        private int totalPoints;

        // Getters and setters
        public int getTotalContributors() { return totalContributors; }
        public void setTotalContributors(int value) { this.totalContributors = value; }
        public int getTotalMaterials() { return totalMaterials; }
        public void setTotalMaterials(int value) { this.totalMaterials = value; }
        public int getTotalPoints() { return totalPoints; }
        public void setTotalPoints(int value) { this.totalPoints = value; }
    }
}