package com.iskonnect.utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;
    private static final Dotenv dotenv = Dotenv.load();

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Set up connection properties
                Properties props = new Properties();
                props.setProperty("user", dotenv.get("DB_USER"));
                props.setProperty("password", dotenv.get("DB_PASSWORD"));
                props.setProperty("ssl", "true");
                props.setProperty("sslmode", "require");

                // Load driver and connect
                Class.forName("org.postgresql.Driver");
                System.out.println("PostgreSQL JDBC Driver loaded.");

                String url = dotenv.get("DB_URL");
                System.out.println("Attempting to connect to database...");
                
                connection = DriverManager.getConnection(url, props);
                System.out.println("Database connected successfully!");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}