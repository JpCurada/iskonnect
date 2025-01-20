package com.iskonnect.services;

import com.iskonnect.models.Material;
import com.iskonnect.utils.DatabaseConnection;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MaterialService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String SUPABASE_BUCKET_NAME = dotenv.get("SUPABASE_BUCKET_NAME");
    private static final String SUPABASE_ACCESS_KEY = dotenv.get("SUPABASE_ACCESS_KEY");
    private static final String SUPABASE_URL = dotenv.get("SUPABASE_URL");

    public UserStats getUserStats(String userId) throws Exception {
        UserStats stats = new UserStats();
        String pointsQuery = "SELECT points FROM users WHERE user_id = ?";
        String materialsQuery = "SELECT COUNT(*) FROM materials WHERE uploader_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get user points
            try (PreparedStatement stmt = conn.prepareStatement(pointsQuery)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setPoints(rs.getInt("points"));
                    }
                }
            }

            // Get materials count
            try (PreparedStatement stmt = conn.prepareStatement(materialsQuery)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setMaterialsCount(rs.getInt(1));
                    }
                }
            }

            // Clean up prepared statements
            try (PreparedStatement deallocateStmt = conn.prepareStatement("DEALLOCATE ALL")) {
                deallocateStmt.execute();
            }

        }
        return stats;
    }

    public void uploadMaterial(String userId, MaterialUploadRequest request) throws Exception {
        String modifiedFilename = generateFileName(request.getFile().getName());
        String objectPath = "reviewers/" + modifiedFilename;
        String fileUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET_NAME + "/" + objectPath;

        // Upload file to Supabase storage
        uploadFileToSupabase(request.getFile(), objectPath);

        // Save material and update points in a single transaction
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            try (PreparedStatement materialStmt = conn.prepareStatement("""
                INSERT INTO materials (title, description, subject, college, course, file_url, 
                                       filename, uploader_id, upload_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """)) {
                // Save material to database
                materialStmt.setString(1, request.getMaterialName());
                materialStmt.setString(2, request.getDescription());
                materialStmt.setString(3, request.getSubject());
                materialStmt.setString(4, request.getCollege());
                materialStmt.setString(5, request.getCourse());
                materialStmt.setString(6, fileUrl);
                materialStmt.setString(7, modifiedFilename);
                materialStmt.setString(8, userId);
                materialStmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                materialStmt.executeUpdate();

                try (PreparedStatement pointsStmt = conn.prepareStatement("""
                    UPDATE users SET points = points + ? WHERE user_id = ?
                """)) {
                    // Update user points
                    pointsStmt.setInt(1, 5);
                    pointsStmt.setString(2, userId);
                    pointsStmt.executeUpdate();
                }

                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                throw e;
            } finally {
                try (PreparedStatement deallocateStmt = conn.prepareStatement("DEALLOCATE ALL")) {
                    deallocateStmt.execute();
                }
            }
        }
    }

    private void uploadFileToSupabase(File file, String objectPath) throws Exception {
        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET_NAME + "/" + objectPath;
        HttpURLConnection connection = (HttpURLConnection) URI.create(uploadUrl).toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ACCESS_KEY);
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setRequestProperty("x-upsert", "true");

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = connection.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            String errorMessage = "Upload failed with response code: " + responseCode;
            throw new Exception(errorMessage);
        }
    }

    public List<Material> getAllMaterials() throws Exception {
        List<Material> materials = new ArrayList<>();
        String query = """
            SELECT 
                m.*,
                u.first_name,
                u.last_name,
                COALESCE((SELECT COUNT(*) 
                          FROM votes v 
                          WHERE v.material_id = m.material_id AND v.vote_type = 'UPVOTE'), 0) as upvotes
            FROM materials m
            JOIN users u ON m.uploader_id = u.user_id
            ORDER BY m.upload_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Material material = new Material(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("subject"),
                    rs.getString("college"),
                    rs.getString("course"),
                    rs.getString("uploader_id")
                );
                materials.add(material);
            }

            try (PreparedStatement deallocateStmt = conn.prepareStatement("DEALLOCATE ALL")) {
                deallocateStmt.execute();
            }
        }
        return materials;
    }

    private String generateFileName(String originalFilename) {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmm"));
        return datePart + timePart + "_" + originalFilename;
    }

    public static class UserStats {
        private int points;
        private int materialsCount;

        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }
        public int getMaterialsCount() { return materialsCount; }
        public void setMaterialsCount(int count) { this.materialsCount = count; }
    }

    public static class MaterialUploadRequest {
        private final String materialName;
        private final String description;
        private final String subject;
        private final String college;
        private final String course;
        private final File file;

        public MaterialUploadRequest(String materialName, String description, String subject,
                                     String college, String course, File file) {
            this.materialName = materialName;
            this.description = description;
            this.subject = subject;
            this.college = college;
            this.course = course;
            this.file = file;
        }

        public String getMaterialName() { return materialName; }
        public String getDescription() { return description; }
        public String getSubject() { return subject; }
        public String getCollege() { return college; }
        public String getCourse() { return course; }
        public File getFile() { return file; }
    }
}
