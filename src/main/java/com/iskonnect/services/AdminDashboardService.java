package com.iskonnect.services;

import java.sql.*;
import java.util.*;

public class AdminDashboardService extends BaseService {

    public DashboardStats getDashboardStats(String timeRange) throws SQLException {
        String query = """
            SELECT 
                (SELECT COUNT(*) FROM users WHERE user_type = 'STUDENT') as total_students,  -- No time filter here
                (SELECT COUNT(*) FROM materials WHERE %s) as total_materials,
                (SELECT COUNT(DISTINCT material_id) FROM reports WHERE %s) as reported_materials
            """;

        query = query.formatted(getTimeFilter("materials", timeRange), getTimeFilter("reports", timeRange));

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new DashboardStats(
                        rs.getInt("total_students"),
                        rs.getInt("total_materials"),
                        rs.getInt("reported_materials")
                    );
                }
            }
        } finally {
            closeConnection();
        }
        return new DashboardStats(0, 0, 0);
    }

    public List<MaterialStat> getTopMaterials(int limit, String timeRange) throws SQLException {
        String query = """
            SELECT m.title, COUNT(v.vote_id) as upvotes 
            FROM materials m 
            LEFT JOIN votes v ON m.material_id = v.material_id 
            WHERE v.vote_type = 'UPVOTE' AND %s
            GROUP BY m.material_id, m.title 
            ORDER BY upvotes DESC 
            LIMIT ?
            """;

        query = String.format(query, getTimeFilter("m", timeRange)); // Use "m" as the alias

        List<MaterialStat> stats = new ArrayList<>();
        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    stats.add(new MaterialStat(
                        rs.getString("title"),
                        rs.getInt("upvotes")
                    ));
                }
            }
        } finally {
            closeConnection();
        }
        return stats;
    }

    public UserDistribution getUserDistribution(String timeRange) throws SQLException {
        String query = """
            SELECT 
                COUNT(DISTINCT CASE WHEN m.material_id IS NOT NULL AND %s THEN u.user_id END) as active_users,
                COUNT(DISTINCT CASE WHEN m.material_id IS NULL OR NOT %s THEN u.user_id END) as inactive_users
            FROM users u 
            LEFT JOIN materials m ON u.user_id = m.uploader_id
            WHERE u.user_type = 'STUDENT'
            """;

        query = String.format(query, getTimeFilter("m", timeRange), getTimeFilter("m", timeRange));

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new UserDistribution(
                        rs.getInt("active_users"),
                        rs.getInt("inactive_users")
                    );
                }
            }
        } finally {
            closeConnection();
        }
        return new UserDistribution(0, 0);
    }

    public List<ContributorStat> getTopContributors(int limit, String timeRange) throws SQLException {
        String query = """
            SELECT 
                u.first_name || ' ' || u.last_name as contributor_name,
                COUNT(v.vote_id) as received_upvotes
            FROM users u 
            JOIN materials m ON u.user_id = m.uploader_id
            LEFT JOIN votes v ON m.material_id = v.material_id
            WHERE v.vote_type = 'UPVOTE' AND %s
            GROUP BY u.user_id, contributor_name
            ORDER BY received_upvotes DESC
            LIMIT ?
            """;

        query = String.format(query, getTimeFilter("m", timeRange));  // Use "m" as the alias

        List<ContributorStat> stats = new ArrayList<>();
        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    stats.add(new ContributorStat(
                        rs.getString("contributor_name"),
                        rs.getInt("received_upvotes")
                    ));
                }
            }
        } finally {
                closeConnection();
            }
        return stats;
    }

    public List<CollegeStat> getCollegeDistribution(String timeRange) throws SQLException {
        String query = """
            SELECT college, COUNT(*) as material_count
            FROM materials
            WHERE %s
            GROUP BY college
            ORDER BY material_count DESC
            """;

        query = String.format(query, getTimeFilter("materials", timeRange));

        List<CollegeStat> stats = new ArrayList<>();
        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    stats.add(new CollegeStat(
                        rs.getString("college"),
                        rs.getInt("material_count")
                    ));
                }
            }
        } finally {
            closeConnection();
        }
        return stats;
    }

    private String getTimeFilter(String tableAlias, String timeRange) {
        return switch (timeRange) {
            case "Past Week" -> tableAlias + (tableAlias.equals("reports") ? ".created_at" : ".upload_date") + " >= NOW() - INTERVAL '7 days'";
            case "Past Month" -> tableAlias + (tableAlias.equals("reports") ? ".created_at" : ".upload_date") + " >= NOW() - INTERVAL '1 month'";
            case "Past Year" -> tableAlias + (tableAlias.equals("reports") ? ".created_at" : ".upload_date") + " >= NOW() - INTERVAL '1 year'";
            default -> "1=1"; // No filtering for "All Time"
        };
    }

    // Data Classes
    public static class DashboardStats {
        private final int totalStudents;
        private final int totalMaterials;
        private final int reportedMaterials;

        public DashboardStats(int totalStudents, int totalMaterials, int reportedMaterials) {
            this.totalStudents = totalStudents;
            this.totalMaterials = totalMaterials;
            this.reportedMaterials = reportedMaterials;
        }

        public int getTotalStudents() { return totalStudents; }
        public int getTotalMaterials() { return totalMaterials; }
        public int getReportedMaterials() { return reportedMaterials; }
    }

    public static class MaterialStat {
        private final String title;
        private final int upvotes;

        public MaterialStat(String title, int upvotes) {
            this.title = title;
            this.upvotes = upvotes;
        }

        public String getTitle() { return title; }
        public int getUpvotes() { return upvotes; }
    }

    public static class UserDistribution {
        private final int activeUsers;
        private final int inactiveUsers;

        public UserDistribution(int activeUsers, int inactiveUsers) {
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
        }

        public int getActiveUsers() { return activeUsers; }
        public int getInactiveUsers() { return inactiveUsers; }
    }

    public static class ContributorStat {
        private final String name;
        private final int upvotes;

        public ContributorStat(String name, int upvotes) {
            this.name = name;
            this.upvotes = upvotes;
        }

        public String getName() { return name; }
        public int getUpvotes() { return upvotes; }
    }

    public static class CollegeStat {
        private final String collegeName;
        private final int materialCount;

        public CollegeStat(String collegeName, int materialCount) {
            this.collegeName = collegeName;
            this.materialCount = materialCount;
        }

        public String getCollegeName() { return collegeName; }
        public int getMaterialCount() { return materialCount; }
    }
}