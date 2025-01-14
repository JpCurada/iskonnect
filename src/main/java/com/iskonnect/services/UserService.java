// package com.iskonnect.services;

// import java.sql.*;

// public class UserService {
//     public static boolean authenticateUser(String email, String password) {
//         String hashedPassword = DatabaseConnection.hashPassword(password);
//         String query = """
//             SELECT u.user_type 
//             FROM user_credentials uc 
//             JOIN users u ON uc.user_id = u.user_id 
//             WHERE uc.email = ? AND uc.password_hash = ?
//         """;

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement stmt = conn.prepareStatement(query)) {
            
//             stmt.setString(1, email);
//             stmt.setString(2, hashedPassword);
            
//             ResultSet rs = stmt.executeQuery();
//             return rs.next();
//         } catch (Exception e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     public static boolean registerUser(String email, String password, String username) {
//         String hashedPassword = DatabaseConnection.hashPassword(password);
        
//         try (Connection conn = DatabaseConnection.getConnection()) {
//             conn.setAutoCommit(false);
            
//             // Insert credentials
//             String credQuery = "INSERT INTO user_credentials (email, password_hash) VALUES (?, ?) RETURNING user_id";
//             try (PreparedStatement stmt = conn.prepareStatement(credQuery)) {
//                 stmt.setString(1, email);
//                 stmt.setString(2, hashedPassword);
//                 ResultSet rs = stmt.executeQuery();
                
//                 if (rs.next()) {
//                     int userId = rs.getInt(1);
                    
//                     // Insert user details
//                     String userQuery = "INSERT INTO users (user_id, username, user_type, points) VALUES (?, ?, 'STUDENT', 0)";
//                     try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
//                         userStmt.setInt(1, userId);
//                         userStmt.setString(2, username);
//                         userStmt.executeUpdate();
//                     }
//                 }
//             }
            
//             conn.commit();
//             return true;
//         } catch (Exception e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     public static boolean isAdmin(String email) {
//         String query = """
//             SELECT 1 FROM user_credentials uc 
//             JOIN users u ON uc.user_id = u.user_id 
//             WHERE uc.email = ? AND u.user_type = 'ADMIN'
//         """;

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement stmt = conn.prepareStatement(query)) {
            
//             stmt.setString(1, email);
//             ResultSet rs = stmt.executeQuery();
//             return rs.next();
//         } catch (Exception e) {
//             e.printStackTrace();
//             return false;
//         }
//     }
// }