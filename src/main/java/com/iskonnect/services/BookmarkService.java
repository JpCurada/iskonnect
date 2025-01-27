// Path: src/main/java/com/iskonnect/services/BookmarkService.java

package com.iskonnect.services;

import com.iskonnect.models.Bookmark;
import com.iskonnect.models.Material;
import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookmarkService {
    
    public boolean addBookmark(int materialId, String userId) {
        String query = "INSERT INTO bookmarks (material_id, user_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeBookmark(int materialId, String userId) {
        String query = "DELETE FROM bookmarks WHERE material_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isBookmarked(int materialId, String userId) {
        String query = "SELECT COUNT(*) FROM bookmarks WHERE material_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Material> getBookmarkedMaterials(String userId) {
        List<Material> materials = new ArrayList<>();
        String query = """
            SELECT 
                m.*,
                u.first_name,
                u.last_name,
                b.created_at as bookmark_date,
                COALESCE(COUNT(v.vote_id), 0) as upvotes
            FROM materials m
            JOIN bookmarks b ON m.material_id = b.material_id
            JOIN users u ON m.uploader_id = u.user_id
            LEFT JOIN votes v ON m.material_id = v.material_id AND v.vote_type = 'UPVOTE'
            WHERE b.user_id = ?
            GROUP BY 
                m.material_id,
                m.title,
                m.description,
                m.subject,
                m.college,
                m.course,
                m.file_url,
                m.filename,
                m.uploader_id,
                m.upload_date,
                u.first_name,
                u.last_name,
                b.created_at
            ORDER BY b.created_at DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
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
                material.setUpvotes(rs.getInt("upvotes"));
                
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return materials;
    }
}