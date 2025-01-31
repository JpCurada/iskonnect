package com.iskonnect.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final HikariDataSource dataSource;
    private static final Dotenv dotenv = Dotenv.load();

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dotenv.get("DB_URL"));
        config.setUsername(dotenv.get("DB_USER"));
        config.setPassword(dotenv.get("DB_PASSWORD"));

        // 🔹 Adjusted connection pool settings for low max connections
        int maxConnections = 10; // Default max pool size
        try {
            maxConnections = Math.min(getDatabaseMaxConnections() / 2, 10); // Use half of DB max
        } catch (Exception e) {
            System.err.println("[DB] Warning: Could not determine max_connections, using default: " + maxConnections);
        }

        config.setMaximumPoolSize(maxConnections); // Dynamic pool size
        config.setMinimumIdle(Math.max(2, maxConnections / 3)); // Maintain some idle connections
        config.setIdleTimeout(15000); // Lower idle timeout for quick recycling
        config.setConnectionTimeout(5000); // Lower timeout to avoid blocking long waits
        config.setMaxLifetime(900000); // Shorten max lifetime to prevent stale connections (15 min)

        // 🔹 Connection Health Checks
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000); // Quick validation for new connections

        // 🔥 Leak Detection: Faster detection of leaked connections
        config.setLeakDetectionThreshold(15000); // Reduce leak detection time (15s)

        // 🔹 Enable Connection Eviction (remove stale connections)
        config.addDataSourceProperty("maximumPoolSize", maxConnections);
        config.addDataSourceProperty("minimumIdle", Math.max(2, maxConnections / 3));
        config.addDataSourceProperty("idleTimeout", "15000");
        config.addDataSourceProperty("maxLifetime", "900000");
        config.addDataSourceProperty("testWhileIdle", "true");
        config.addDataSourceProperty("testOnBorrow", "true");

        // ✅ Initialize the HikariCP DataSource
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        logConnectionStatus();
        return conn;
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DB] Connection pool closed.");
        }
    }

    private static void logConnectionStatus() {
        System.out.println("[DB] Active: " + dataSource.getHikariPoolMXBean().getActiveConnections());
        System.out.println("[DB] Idle: " + dataSource.getHikariPoolMXBean().getIdleConnections());
        System.out.println("[DB] Total: " + dataSource.getHikariPoolMXBean().getTotalConnections());
    }

    // 🔹 Dynamically fetch max_connections from the database
    private static int getDatabaseMaxConnections() {
        int maxConnections = 10; // Default value
        try (Connection conn = dataSource.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SHOW max_connections")) {
            if (rs.next()) {
                maxConnections = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Could not fetch max_connections: " + e.getMessage());
        }
        return maxConnections;
    }
}
