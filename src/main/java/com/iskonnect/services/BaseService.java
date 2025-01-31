package com.iskonnect.services;

import com.iskonnect.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseService {
    private Connection connection;

    protected void openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getConnection();
        }
    }

    protected Connection getConnection() {
        return connection;
    }

    protected void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null; // Reset connection to null after closing
            }
        }
    }

    protected void beginTransaction() throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }

    protected void commitTransaction() throws SQLException {
        if (connection != null) {
            connection.commit();
        }
    }

    protected void rollbackTransaction() {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}