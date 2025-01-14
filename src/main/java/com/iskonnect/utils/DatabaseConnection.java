package com.iskonnect.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.InetAddress;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://db.nhepnvpgxzfexwuswyyw.supabase.co:5432/postgres?sslmode=require&user=postgres&password=55Oj5Y4Z6mH9hmhC";
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // First check if we can reach the host
                System.out.println("Trying to reach database host...");
                InetAddress address = InetAddress.getByName("db.nhepnvpgxzfexwuswyyw.supabase.co");
                System.out.println("Host IP: " + address.getHostAddress());

                // Then try database connection
                System.out.println("Loading PostgreSQL driver...");
                Class.forName("org.postgresql.Driver");
                
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(URL);  // No need for user and password here
                System.out.println("Database connected!");
            }
            return connection;
        } catch (java.net.UnknownHostException e) {
            System.out.println("Cannot resolve database hostname. DNS issue detected.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
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
}