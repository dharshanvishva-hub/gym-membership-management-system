package com.gym.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton database connection manager for SQLite.
 * Ensures foreign keys are enabled and manages database connection lifecycle.
 */
public class DatabaseConnection {
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:database/gym.db";
    private static Connection connection = null;

    private DatabaseConnection() {
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Ensure parent directory exists
                File dbDir = new File("database");
                if (!dbDir.exists()) {
                    dbDir.mkdirs();
                }
                
                // Explicitly load SQLite JDBC driver class
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DEFAULT_DB_URL);
                
                // Enable SQLite Foreign Key constraints
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC Driver not found in classpath. Ensure sqlite-jdbc jar is included.", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            } finally {
                connection = null;
            }
        }
    }
}
