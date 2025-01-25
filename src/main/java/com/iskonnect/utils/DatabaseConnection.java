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

        // Connection pooling settings
        config.setMaximumPoolSize(10); // Adjust based on your application needs
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000); // 30 seconds
        config.setConnectionTimeout(20000); // 20 seconds
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTestQuery("SELECT 1"); // Validate connections

        // Prepared statement caching settings
        config.addDataSourceProperty("useServerPrepStmts", "false"); // Disable server-side prepared statements
        config.addDataSourceProperty("cachePrepStmts", "true");      // Enable local statement caching
        config.addDataSourceProperty("prepStmtCacheSize", "250");    // Cache up to 250 statements
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // Limit SQL length for caching

        // Connection reset on return to pool
        config.setInitializationFailTimeout(0); // Don't fail if DB is down on startup
        config.addDataSourceProperty("cachePrepStmts", "true"); // Caches prepared statements

        // Set session reset
        config.setConnectionInitSql("DISCARD ALL");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}