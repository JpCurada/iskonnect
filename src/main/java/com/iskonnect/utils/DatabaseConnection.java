package com.iskonnect.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials
    private static final String URL = "jdbc:postgresql://db.nhepnvpgxzfexwuswyyw.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "55Oj5Y4Z6mH9hmhC";
    
    private static Connection connection;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected!");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}