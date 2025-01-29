// Path: src/main/java/com/iskonnect/services/AdminReportService.java
package com.iskonnect.services;

import com.iskonnect.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminReportService {
    
    public static class ReportEntry {
        private final int materialId;
        private final String title;
        private final String description;
        private final String uploaderId;
        private final String reporterId;
        private final String reason;
        private final int reportCount;
        private final String fileUrl;

        public ReportEntry(int materialId, String title, String description, 
                          String uploaderId, String reporterId, String reason, 
                          int reportCount, String fileUrl) {
            this.materialId = materialId;
            this.title = title;
            this.description = description;
            this.uploaderId = uploaderId;
            this.reporterId = reporterId;
            this.reason = reason;
            this.reportCount = reportCount;
            this.fileUrl = fileUrl;
        }

        // Getters
        public int getMaterialId() { return materialId; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getUploaderId() { return uploaderId; }
        public String getReporterId() { return reporterId; }
        public String getReason() { return reason; }
        public int getReportCount() { return reportCount; }
        public String getFileUrl() { return fileUrl; }
    }

    public ObservableList<ReportEntry> getReportedMaterials() throws SQLException {
        ObservableList<ReportEntry> reports = FXCollections.observableArrayList();
        String query = """
            SELECT 
                m.material_id,
                m.title,
                m.description,
                m.uploader_id,
                m.file_url,
                r.reporter_id,
                r.reason,
                COUNT(r.material_id) as report_count
            FROM reports r
            JOIN materials m ON r.material_id = m.material_id
            GROUP BY m.material_id, m.title, m.description, m.uploader_id, 
                     m.file_url, r.reporter_id, r.reason
            ORDER BY report_count DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(new ReportEntry(
                    rs.getInt("material_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("uploader_id"),
                    rs.getString("reporter_id"),
                    rs.getString("reason"),
                    rs.getInt("report_count"),
                    rs.getString("file_url")
                ));
            }
        }
        return reports;
    }

    public boolean deleteMaterial(int materialId) throws SQLException {
    String getUploaderQuery = "SELECT uploader_id FROM materials WHERE material_id = ?";
    String updateUserPointsQuery = "UPDATE users SET points = points - 5 WHERE user_id = ?";
    String checkUserPointsQuery = "SELECT points FROM users WHERE user_id = ?";
    String getBadgesToRevokeQuery = "SELECT badge_id FROM badges WHERE requirement_points > ?";
    String revokeUserBadgeQuery = "DELETE FROM user_badges WHERE user_id = ? AND badge_id = ?";
    String deleteVotesQuery = "DELETE FROM votes WHERE material_id = ?";
    String deleteReportsQuery = "DELETE FROM reports WHERE material_id = ?";
    String deleteBookmarksQuery = "DELETE FROM bookmarks WHERE material_id = ?";
    String deleteMaterialQuery = "DELETE FROM materials WHERE material_id = ?";

    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false); // Start transaction
        try {
            String uploaderId = null;

            // Step 1: Fetch uploader ID
            try (PreparedStatement stmt = conn.prepareStatement(getUploaderQuery)) {
                stmt.setInt(1, materialId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        uploaderId = rs.getString("uploader_id");
                    } else {
                        return false; // Material not found
                    }
                }
            }

            // Step 2: Deduct 5 points from uploader
            try (PreparedStatement stmt = conn.prepareStatement(updateUserPointsQuery)) {
                stmt.setString(1, uploaderId);
                stmt.executeUpdate();
            }

            // Step 3: Check updated user points
            int userPoints = 0;
            try (PreparedStatement stmt = conn.prepareStatement(checkUserPointsQuery)) {
                stmt.setString(1, uploaderId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userPoints = rs.getInt("points");
                    }
                }
            }

            // Step 4: Find badges to revoke (badges with requirement_points > userPoints)
            List<Integer> badgesToRevoke = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(getBadgesToRevokeQuery)) {
                stmt.setInt(1, userPoints);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        badgesToRevoke.add(rs.getInt("badge_id"));
                    }
                }
            }

            // Step 5: Revoke badges that the user no longer qualifies for
            for (int badgeId : badgesToRevoke) {
                try (PreparedStatement stmt = conn.prepareStatement(revokeUserBadgeQuery)) {
                    stmt.setString(1, uploaderId);
                    stmt.setInt(2, badgeId);
                    stmt.executeUpdate();
                }
            }

            // Step 6: Delete votes, reports, bookmarks, and the material
            try (PreparedStatement stmt = conn.prepareStatement(deleteVotesQuery)) {
                stmt.setInt(1, materialId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteReportsQuery)) {
                stmt.setInt(1, materialId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteBookmarksQuery)) {
                stmt.setInt(1, materialId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteMaterialQuery)) {
                stmt.setInt(1, materialId);
                int result = stmt.executeUpdate();
                conn.commit(); // Commit transaction if all operations succeed
                return result > 0;
            }
        } catch (SQLException e) {
            conn.rollback(); // Rollback transaction if any step fails
            throw e;
        }
    }
}
    
}