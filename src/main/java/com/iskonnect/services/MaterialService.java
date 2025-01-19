// Path: src/main/java/com/iskonnect/services/MaterialService.java

package com.iskonnect.services;

import com.iskonnect.utils.DatabaseConnection;
import com.iskonnect.models.Material;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.*;


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
            // Get points
            try (PreparedStatement stmt = conn.prepareStatement(pointsQuery)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.setPoints(rs.getInt("points"));
                }
            }

            // Get materials count
            try (PreparedStatement stmt = conn.prepareStatement(materialsQuery)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.setMaterialsCount(rs.getInt(1));
                }
            }
        }
        return stats;
    }

    public void uploadMaterial(String userId, MaterialUploadRequest request) throws Exception {
        String modifiedFilename = generateFileName(request.getFile().getName());
        String objectPath = "reviewers/" + modifiedFilename;
        String fileUrl = "https://nhepnvpgxzfexwuswyyw.supabase.co/storage/v1/object/public/iskonnect-materials/" + objectPath;

        // Upload file to Supabase storage
        uploadFileToSupabase(request.getFile(), objectPath);

        // Save to database using your existing connection
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (saveMaterialToDatabase(conn, userId, request, fileUrl, modifiedFilename) &&
                    updateUserPoints(conn, userId, 5)) {
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new Exception("Failed to save material");
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private void uploadFileToSupabase(File file, String objectPath) throws Exception {
        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" +
            SUPABASE_BUCKET_NAME + "/" + objectPath;

        HttpURLConnection connection = (HttpURLConnection) URI.create(uploadUrl).toURL().openConnection();
        connection.setRequestMethod("POST");  // Changed from PUT to POST
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ACCESS_KEY);
        connection.setRequestProperty("Content-Type", "application/octet-stream");  // Changed content type
        
        // Add these headers
        connection.setRequestProperty("x-upsert", "true");  // Allow overwriting if file exists
        
        // Get file size and set content length
        long fileSize = file.length();
        connection.setRequestProperty("Content-Length", String.valueOf(fileSize));

        try (FileInputStream fis = new FileInputStream(file);
            OutputStream os = connection.getOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                
                // You could add progress tracking here if needed
                double progress = (double) totalBytesRead / fileSize;
                System.out.println("Upload progress: " + (progress * 100) + "%");
            }
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        
        // Print detailed error information
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Upload failed with response code: ").append(responseCode);
            
            // Try to read error stream
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    errorMessage.append("\nError details: ").append(line);
                }
            } catch (Exception e) {
                errorMessage.append("\nCould not read error details: ").append(e.getMessage());
            }
            
            throw new Exception(errorMessage.toString());
        }

        // Check if file was actually uploaded
        verifyFileUpload(uploadUrl);
    }

    private void verifyFileUpload(String uploadUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) URI.create(uploadUrl).toURL().openConnection();
        connection.setRequestMethod("HEAD");
        connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ACCESS_KEY);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("File upload verification failed. File might not have been uploaded correctly.");
        }
    }


    private boolean saveMaterialToDatabase(Connection conn, String userId, MaterialUploadRequest request, 
                                         String fileUrl, String filename) throws Exception {
        String query = """
            INSERT INTO materials (title, description, subject, college, course, file_url, 
                                 filename, uploader_id, upload_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, request.getMaterialName());
            stmt.setString(2, request.getDescription());
            stmt.setString(3, request.getSubject());
            stmt.setString(4, request.getCollege());
            stmt.setString(5, request.getCourse());
            stmt.setString(6, fileUrl);
            stmt.setString(7, filename);
            stmt.setString(8, userId);
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean updateUserPoints(Connection conn, String userId, int points) throws Exception {
        String query = "UPDATE users SET points = points + ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, points);
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    private String generateFileName(String originalFilename) {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmm"));
        return datePart + timePart + "_" + originalFilename;
    }

    // Inner classes remain the same
    public static class UserStats {
        private int points;
        private int materialsCount;

        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }
        public int getMaterialsCount() { return materialsCount; }
        public void setMaterialsCount(int count) { this.materialsCount = count; }
    }

    public List<Material> getAllMaterials() throws Exception {
        List<Material> materials = new ArrayList<>();
        String query = """
            SELECT 
                m.*,
                u.first_name,
                u.last_name,
                COALESCE((
                    SELECT COUNT(*) 
                    FROM votes v 
                    WHERE v.material_id = m.material_id 
                    AND v.vote_type = 'UPVOTE'
                ), 0) as upvotes
            FROM materials m
            JOIN users u ON m.uploader_id = u.user_id
            ORDER BY m.upload_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
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
                
                // Set additional properties
                material.setMaterialId(rs.getInt("material_id"));
                material.setFileUrl(rs.getString("file_url"));
                material.setFileName(rs.getString("filename"));
                material.setUploadDate(rs.getTimestamp("upload_date").toLocalDateTime());
                
                // Set uploader name
                String uploaderName = rs.getString("first_name") + " " + rs.getString("last_name");
                material.setUploaderName(uploaderName);
                
                // Set vote count
                material.setUpvotes(rs.getInt("upvotes"));

                materials.add(material);
            }
        }
        return materials;
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

        // Getters
        public String getMaterialName() { return materialName; }
        public String getDescription() { return description; }
        public String getSubject() { return subject; }
        public String getCollege() { return college; }
        public String getCourse() { return course; }
        public File getFile() { return file; }
    }
}