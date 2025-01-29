package com.iskonnect.services;

import com.iskonnect.controllers.BadgeNotificationController;
import com.iskonnect.models.Badge;
import com.iskonnect.models.Material;
import com.iskonnect.utils.DatabaseConnection;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
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

    public UserStats getUserStats(String userId) {
        UserStats stats = new UserStats();
        String pointsQuery = "SELECT points FROM users WHERE user_id = ?";
        String materialsQuery = "SELECT COUNT(*) FROM materials WHERE uploader_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(pointsQuery)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setPoints(rs.getInt("points"));
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(materialsQuery)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setMaterialsCount(rs.getInt(1));
                    }
                }
            }

            // Explicitly deallocate prepared statements (if necessary)
            try (PreparedStatement deallocateStmt = conn.prepareStatement("DEALLOCATE ALL")) {
                deallocateStmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public void uploadMaterial(String userId, MaterialUploadRequest request) throws Exception {
        // Generate the filename with date and time
        String modifiedFilename = generateFileName(request.getFile().getName());
        
        // URL encode the filename to handle spaces and special characters
        String encodedFilename = encodeFilename(modifiedFilename);
        
        String objectPath = "reviewers/" + encodedFilename;
        String fileUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET_NAME + "/" + objectPath;

        // Upload file first
        uploadFileToSupabase(request.getFile(), objectPath);

        Connection conn = null;
        PreparedStatement materialStmt = null;
        PreparedStatement pointsStmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert material
            String materialSql = """
                INSERT INTO materials (title, description, subject, college, course, file_url, 
                                    uploader_id, upload_date, filename)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            
            materialStmt = conn.prepareStatement(materialSql);
            materialStmt.setString(1, request.getMaterialName());
            materialStmt.setString(2, request.getDescription());
            materialStmt.setString(3, request.getSubject());
            materialStmt.setString(4, request.getCollege());
            materialStmt.setString(5, request.getCourse());
            materialStmt.setString(6, fileUrl);
            materialStmt.setString(7, userId);
            materialStmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            materialStmt.setString(9, encodedFilename);
            materialStmt.executeUpdate();

            // Update points and retrieve updated points
            String pointsSql = "UPDATE users SET points = points + 5 WHERE user_id = ? RETURNING points";
            pointsStmt = conn.prepareStatement(pointsSql);
            pointsStmt.setString(1, userId);
            rs = pointsStmt.executeQuery();
            
            int updatedPoints = 0;
            if (rs.next()) {
                updatedPoints = rs.getInt("points");
            }

            // Evaluate and award badges
            BadgeService badgeService = new BadgeService();
            List<Badge> awardedBadges = badgeService.evaluateAndAwardBadges(userId, updatedPoints);

            // Show badge notifications
            if (!awardedBadges.isEmpty()) {
                showBadgeNotification(awardedBadges);
            }

            // Commit transaction
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Close all resources in reverse order
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pointsStmt != null) {
                try {
                    pointsStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (materialStmt != null) {
                try {
                    materialStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showBadgeNotification(List<Badge> awardedBadges) {
        for (Badge badge : awardedBadges) {
            // Load FXML for the notification dialog
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/BadgeNotification.fxml"));
                Parent root = loader.load();

                // Set badge details in the controller
                BadgeNotificationController controller = loader.getController();
                controller.setBadgeDetails(badge.getName(), badge.getDescription(), badge.getImageUrl());

                // Create a new stage for the dialog
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Congratulations!");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
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
            throw new Exception("Upload failed with response code: " + responseCode);
        }
    }

    public List<Material> getAllMaterials() {
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
                
                // Make sure we set the uploader's full name
                material.setUploaderName(rs.getString("first_name") + " " + rs.getString("last_name"));
                material.setMaterialId(rs.getInt("material_id"));
                material.setFileUrl(rs.getString("file_url"));       
                material.setFileName(rs.getString("filename"));       
                material.setUploadDate(rs.getTimestamp("upload_date").toLocalDateTime());
                material.setUpvotes(rs.getInt("upvotes"));          
                
                materials.add(material);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    private String generateFileName(String originalFilename) {
        String cleanFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return timestamp + "_" + cleanFilename;
    }

    private String encodeFilename(String originalFilename) throws UnsupportedEncodingException {
        return URLEncoder.encode(originalFilename, "UTF-8");
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
    
        // Corrected constructor name
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