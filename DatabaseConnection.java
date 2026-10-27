package com.hostel.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Class: DatabaseConnection
 * Demonstrates:
 * 1. Design Pattern: Creational Singleton Pattern ensuring a single connection coordinator.
 * 2. JDBC Connectivity: Establishes and tests connection to MySQL.
 * 3. Graceful Fallback: Detects availability to ensure 100% test reliability.
 * 4. Configurable Ports & Multi-Device TLS Security Settings.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Properties properties;
    private boolean isMockMode = false;
    private String statusMessage = "Uninitialized";

    private DatabaseConnection() {
        loadProperties();
        testConnection();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        // Default properties
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hostel_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "root");
        properties.setProperty("server.port", "8443");
        properties.setProperty("server.ssl.enabled", "true");
        properties.setProperty("server.ssl.keystore", "config/keystore.p12");
        properties.setProperty("server.ssl.password", "hostelpass123");
        properties.setProperty("db.enable.mock.fallback", "true");

        // Try multiple locations for db.properties
        File[] candidatePaths = {
            new File("database/db.properties"),
            new File("db.properties"),
            new File("../database/db.properties"),
            new File("config/db.properties")
        };

        boolean loaded = false;
        for (File candidate : candidatePaths) {
            if (candidate.exists()) {
                try (InputStream input = new FileInputStream(candidate)) {
                    properties.load(input);
                    System.out.println("[DatabaseConnection] Loaded configuration from: " + candidate.getAbsolutePath());
                    loaded = true;
                    break;
                } catch (Exception e) {
                    System.err.println("[DatabaseConnection] Could not read " + candidate.getName() + ": " + e.getMessage());
                }
            }
        }

        if (!loaded) {
            System.out.println("[DatabaseConnection] Using default database & security properties.");
        }

        // JVM System Property overrides (e.g. -Dserver.port=9090)
        String sysPort = System.getProperty("server.port");
        if (sysPort != null && !sysPort.trim().isEmpty()) {
            properties.setProperty("server.port", sysPort.trim());
        }

        String sysSsl = System.getProperty("server.ssl.enabled");
        if (sysSsl != null && !sysSsl.trim().isEmpty()) {
            properties.setProperty("server.ssl.enabled", sysSsl.trim());
        }
    }

    private void testConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String pass = properties.getProperty("db.password");

            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                if (conn != null && !conn.isClosed()) {
                    isMockMode = false;
                    statusMessage = "CONNECTED (MySQL 8.0 @ localhost:3306/hostel_db)";
                    System.out.println("=================================================");
                    System.out.println("[SUCCESS] Database Connection Established!");
                    System.out.println("Status: " + statusMessage);
                    System.out.println("=================================================");
                    return;
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL Driver not found on classpath: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] MySQL Connection attempt failed: " + e.getMessage());
            System.err.println("[DatabaseConnection] Tip: Verify user/password in database/db.properties or execute database/schema.sql in MySQL Workbench.");
        }

        boolean fallbackEnabled = Boolean.parseBoolean(properties.getProperty("db.enable.mock.fallback", "true"));
        if (fallbackEnabled) {
            isMockMode = true;
            statusMessage = "ACTIVE (Self-Contained Mock Repository for Demonstration)";
            System.out.println("=================================================");
            System.out.println("[NOTE] MySQL server offline or credentials pending.");
            System.out.println("[NOTE] Engaging In-Memory Repository for seamless viva presentation!");
            System.out.println("Status: " + statusMessage);
            System.out.println("=================================================");
        } else {
            isMockMode = false;
            statusMessage = "DISCONNECTED (MySQL Error)";
        }
    }

    public Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }

    public boolean isMockMode() {
        return isMockMode;
    }

    public void setMockMode(boolean mockMode) {
        this.isMockMode = mockMode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getPort() {
        try {
            return Integer.parseInt(properties.getProperty("server.port", "8443"));
        } catch (NumberFormatException e) {
            return 8443;
        }
    }

    public void setPort(int port) {
        properties.setProperty("server.port", String.valueOf(port));
    }

    public boolean isSslEnabled() {
        return Boolean.parseBoolean(properties.getProperty("server.ssl.enabled", "true"));
    }

    public void setSslEnabled(boolean enabled) {
        properties.setProperty("server.ssl.enabled", String.valueOf(enabled));
    }

    public String getKeystorePath() {
        return properties.getProperty("server.ssl.keystore", "config/keystore.p12");
    }

    public String getKeystorePassword() {
        return properties.getProperty("server.ssl.password", "hostelpass123");
    }
}
