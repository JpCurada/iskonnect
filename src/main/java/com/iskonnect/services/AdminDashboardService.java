// Path: src/main/java/com/iskonnect/services/AdminDashboardService.java

package com.iskonnect.services;

import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class AdminDashboardService {
    
    public DashboardStats getDashboardStats() throws SQLException {
        String query = """
            SELECT 
                (SELECT COUNT(*) FROM users WHERE user_type = 'STUDENT') as total_students,
                (SELECT COUNT(*) FROM materials) as total_materials,
                (SELECT COUNT(DISTINCT material_id) FROM reports) as reported_materials
            """;
            
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new DashboardStats(
                    rs.getInt("total_students"),
                    rs.getInt("total_materials"),
                    rs.getInt("reported_materials")
                );
            }
        }
        return new DashboardStats(0, 0, 0);
    }
    
    public List<MaterialStat> getTopMaterials(int limit) throws SQLException {
        String query = """
            SELECT m.title, COUNT(v.vote_id) as upvotes 
            FROM materials m 
            LEFT JOIN votes v ON m.material_id = v.material_id 
            WHERE v.vote_type = 'UPVOTE'
            GROUP BY m.material_id, m.title 
            ORDER BY upvotes DESC 
            LIMIT ?
            """;
            
        List<MaterialStat> stats = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                stats.add(new MaterialStat(
                    rs.getString("title"),
                    rs.getInt("upvotes")
                ));
            }
        }
        return stats;
    }
    
    public UserDistribution getUserDistribution() throws SQLException {
        String query = """
            SELECT 
                COUNT(DISTINCT CASE WHEN m.material_id IS NOT NULL THEN u.user_id END) as active_users,
                COUNT(DISTINCT CASE WHEN m.material_id IS NULL THEN u.user_id END) as inactive_users
            FROM users u 
            LEFT JOIN materials m ON u.user_id = m.uploader_id
            WHERE u.user_type = 'STUDENT'
            """;
            
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserDistribution(
                    rs.getInt("active_users"),
                    rs.getInt("inactive_users")
                );
            }
        }
        return new UserDistribution(0, 0);
    }
    
    public List<ContributorStat> getTopContributors(int limit) throws SQLException {
        String query = """
            SELECT 
                u.first_name || ' ' || u.last_name as contributor_name,
                COUNT(v.vote_id) as received_upvotes
            FROM users u 
            JOIN materials m ON u.user_id = m.uploader_id
            LEFT JOIN votes v ON m.material_id = v.material_id
            WHERE v.vote_type = 'UPVOTE'
            GROUP BY u.user_id, contributor_name
            ORDER BY received_upvotes DESC
            LIMIT ?
            """;
            
        List<ContributorStat> stats = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                stats.add(new ContributorStat(
                    rs.getString("contributor_name"),
                    rs.getInt("received_upvotes")
                ));
            }
        }
        return stats;
    }
    
    public List<CollegeStat> getCollegeDistribution() throws SQLException {
        String query = """
            SELECT college, COUNT(*) as material_count
            FROM materials
            GROUP BY college
            ORDER BY material_count DESC
            """;
            
        List<CollegeStat> stats = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                stats.add(new CollegeStat(
                    rs.getString("college"),
                    rs.getInt("material_count")
                ));
            }
        }
        return stats;
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