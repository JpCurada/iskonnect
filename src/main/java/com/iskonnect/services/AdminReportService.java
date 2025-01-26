// Path: src/main/java/com/iskonnect/services/AdminReportService.java
package com.iskonnect.services;

import com.iskonnect.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

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
            String deleteVotesQuery = "DELETE FROM votes WHERE material_id = ?";
            String deleteReportsQuery = "DELETE FROM reports WHERE material_id = ?";
            String deleteMaterialQuery = "DELETE FROM materials WHERE material_id = ?";

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // Delete votes first
                    try (PreparedStatement stmt = conn.prepareStatement(deleteVotesQuery)) {
                        stmt.setInt(1, materialId);
                        stmt.executeUpdate();
                    }

                    // Delete reports next
                    try (PreparedStatement stmt = conn.prepareStatement(deleteReportsQuery)) {
                        stmt.setInt(1, materialId);
                        stmt.executeUpdate();
                    }

                    // Finally delete the material
                    try (PreparedStatement stmt = conn.prepareStatement(deleteMaterialQuery)) {
                        stmt.setInt(1, materialId);
                        int result = stmt.executeUpdate();
                        conn.commit();
                        return result > 0;
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            }
        }

    }