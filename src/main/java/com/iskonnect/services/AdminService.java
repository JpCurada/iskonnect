package com.iskonnect.services;

import com.iskonnect.models.Material;
import com.iskonnect.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService extends BaseService {

    public AdminStats getAdminStats() throws SQLException {
        AdminStats stats = new AdminStats();
        String query = """
            SELECT 
                (SELECT COUNT(*) FROM users WHERE user_type = 'STUDENT') as total_users,
                (SELECT COUNT(*) FROM materials) as total_materials,
                (SELECT COUNT(*) FROM reports WHERE status = 'PENDING') as pending_reports
            """;

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.setTotalUsers(rs.getInt("total_users"));
                    stats.setTotalMaterials(rs.getInt("total_materials"));
                    stats.setPendingReports(rs.getInt("pending_reports"));
                }
            }
        } finally {
            closeConnection();
        }
        return stats;
    }

    public List<Student> getRecentUsers(int limit) throws SQLException {
        List<Student> users = new ArrayList<>();
        String query = """
            SELECT * FROM users 
            WHERE user_type = 'STUDENT' 
            ORDER BY created_at DESC 
            LIMIT ?
            """;

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Student student = new Student(
                        rs.getString("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                    );
                    users.add(student);
                }
            }
        } finally {
            closeConnection();
        }
        return users;
    }

    public List<Material> getRecentMaterials(int limit) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String query = """
            SELECT 
                m.*,
                u.first_name,
                u.last_name
            FROM materials m
            JOIN users u ON m.uploader_id = u.user_id
            ORDER BY m.upload_date DESC
            LIMIT ?
            """;

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Material material = new Material(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("subject"),
                        rs.getString("college"),
                        rs.getString("course"),
                        rs.getString("uploader_id")
                    );

                    material.setMaterialId(rs.getInt("material_id"));
                    material.setFileUrl(rs.getString("file_url"));
                    material.setFileName(rs.getString("filename"));
                    material.setUploaderName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    material.setUploadDate(rs.getTimestamp("upload_date").toLocalDateTime());

                    materials.add(material);
                }
            }
        } finally {
            closeConnection();
        }
        return materials;
    }

    public boolean removeMaterial(int materialId) throws SQLException {
        String query = "DELETE FROM materials WHERE material_id = ?";

        try {
            openConnection();
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setInt(1, materialId);
                return stmt.executeUpdate() > 0;
            }
        } finally {
            closeConnection();
        }
    }

    public static class AdminStats {
        private int totalUsers;
        private int totalMaterials;
        private int pendingReports;

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        public int getTotalMaterials() { return totalMaterials; }
        public void setTotalMaterials(int totalMaterials) { this.totalMaterials = totalMaterials; }
        public int getPendingReports() { return pendingReports; }
        public void setPendingReports(int pendingReports) { this.pendingReports = pendingReports; }
    }
}