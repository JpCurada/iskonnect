// Path: src/main/java/com/iskonnect/services/ReportService.java

package com.iskonnect.services;

import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;

public class ReportService {
    
    public boolean submitReport(int materialId, String reporterId, String reason, String additionalInfo) {
        String query = """
            INSERT INTO reports (material_id, reporter_id, reason, additional_info, status, created_at)
            VALUES (?, ?, ?, ?, 'PENDING', CURRENT_TIMESTAMP)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, reporterId);
            stmt.setString(3, reason);
            stmt.setString(4, additionalInfo);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hasReported(int materialId, String userId) {
        String query = """
            SELECT COUNT(*) as report_count 
            FROM reports 
            WHERE material_id = ? AND reporter_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("report_count") > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static class ReportStats {
        private int totalReports;
        private int pendingReports;
        private int resolvedReports;
        private int dismissedReports;

        public int getTotalReports() { return totalReports; }
        public void setTotalReports(int totalReports) { this.totalReports = totalReports; }
        public int getPendingReports() { return pendingReports; }
        public void setPendingReports(int pendingReports) { this.pendingReports = pendingReports; }
        public int getResolvedReports() { return resolvedReports; }
        public void setResolvedReports(int resolvedReports) { this.resolvedReports = resolvedReports; }
        public int getDismissedReports() { return dismissedReports; }
        public void setDismissedReports(int dismissedReports) { this.dismissedReports = dismissedReports; }
    }
    
    public ReportStats getReportStats() {
        ReportStats stats = new ReportStats();
        String query = """
            SELECT 
                COUNT(*) as total,
                COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending,
                COUNT(CASE WHEN status = 'RESOLVED' THEN 1 END) as resolved,
                COUNT(CASE WHEN status = 'DISMISSED' THEN 1 END) as dismissed
            FROM reports
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.setTotalReports(rs.getInt("total"));
                stats.setPendingReports(rs.getInt("pending"));
                stats.setResolvedReports(rs.getInt("resolved"));
                stats.setDismissedReports(rs.getInt("dismissed"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public boolean updateReportStatus(int reportId, String status) {
        String query = "UPDATE reports SET status = ? WHERE report_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, reportId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}