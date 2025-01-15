package com.iskonnect.services;

import com.iskonnect.models.Material;
import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialService {
    
    public List<Material> getAllMaterials() {
        List<Material> materials = new ArrayList<>();
        String query = "SELECT * FROM materials ORDER BY upload_date DESC";
        
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
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    public List<Material> searchMaterials(String searchTerm) {
        List<Material> materials = new ArrayList<>();
        String query = """
            SELECT * FROM materials 
            WHERE title ILIKE ? 
            OR description ILIKE ? 
            OR subject ILIKE ?
            ORDER BY upload_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
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
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    public boolean saveMaterial(Material material) {
        String query = """
            INSERT INTO materials (title, description, subject, college, course, file_url, uploader_id) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getDescription());
            stmt.setString(3, material.getSubject());
            stmt.setString(4, material.getCollege());
            stmt.setString(5, material.getCourse());
            stmt.setString(6, material.getFileUrl());
            stmt.setString(7, material.getUploaderId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Material getMaterialById(int materialId) {
        String query = "SELECT * FROM materials WHERE material_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Material(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("subject"),
                    rs.getString("college"),
                    rs.getString("course"),
                    rs.getString("uploader_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Material> getMaterialsByUser(String userId) {
        List<Material> materials = new ArrayList<>();
        String query = "SELECT * FROM materials WHERE uploader_id = ? ORDER BY upload_date DESC";
        
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
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
}