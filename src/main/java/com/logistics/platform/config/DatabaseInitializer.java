package com.logistics.platform.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment env = applicationContext.getEnvironment();
        
        String dbUrl = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        // Use defaults if not set in environment or application.properties
        if (dbUrl == null) {
            dbUrl = "jdbc:postgresql://localhost:5432/easyroute_db";
        }
        if (username == null) {
            username = "postgres";
        }
        if (password == null) {
            password = "root";
        }

        System.out.println("DatabaseInitializer: Initializing PostgreSQL database check...");
        System.out.println("DatabaseInitializer: Target connection URL: " + dbUrl);

        try {
            int doubleSlashIndex = dbUrl.indexOf("//");
            if (doubleSlashIndex != -1) {
                int nextSlashIndex = dbUrl.indexOf("/", doubleSlashIndex + 2);
                if (nextSlashIndex != -1) {
                    String basePart = dbUrl.substring(0, nextSlashIndex); // e.g., jdbc:postgresql://localhost:5432
                    String dbNameAndParams = dbUrl.substring(nextSlashIndex + 1); // e.g., easyroute_db?sslmode=disable
                    
                    int questionMarkIndex = dbNameAndParams.indexOf("?");
                    String dbName = (questionMarkIndex != -1) ? dbNameAndParams.substring(0, questionMarkIndex) : dbNameAndParams;
                    
                    // Connect to default PostgreSQL system database "postgres"
                    String masterUrl = basePart + "/postgres";
                    if (questionMarkIndex != -1) {
                        masterUrl += dbNameAndParams.substring(questionMarkIndex);
                    }
                    
                    createDatabaseIfNotExist(masterUrl, username, password, dbName);
                } else {
                    System.err.println("DatabaseInitializer: Invalid JDBC URL structure (missing database path): " + dbUrl);
                }
            } else {
                System.err.println("DatabaseInitializer: Invalid JDBC URL structure (missing '//'): " + dbUrl);
            }
        } catch (Exception e) {
            System.err.println("DatabaseInitializer: Failed to auto-create database due to an unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDatabaseIfNotExist(String masterUrl, String username, String password, String dbName) {
        String checkQuery = "SELECT 1 FROM pg_database WHERE datname = ?";
        boolean dbExists = false;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("DatabaseInitializer: PostgreSQL JDBC Driver class not found: " + e.getMessage());
            return;
        }

        System.out.println("DatabaseInitializer: Connecting to master database: " + masterUrl);
        try (Connection conn = DriverManager.getConnection(masterUrl, username, password);
             PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dbExists = true;
                    System.out.println("DatabaseInitializer: Database '" + dbName + "' already exists.");
                }
            }
        } catch (SQLException e) {
            System.err.println("DatabaseInitializer: Error checking if database '" + dbName + "' exists: " + e.getMessage());
            // Proceed to attempt creation, or if master database connection failed, we fail gracefully
        }

        if (!dbExists) {
            System.out.println("DatabaseInitializer: Database '" + dbName + "' does not exist. Creating it...");
            String createQuery = "CREATE DATABASE " + dbName;
            try (Connection conn = DriverManager.getConnection(masterUrl, username, password);
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate(createQuery);
                System.out.println("DatabaseInitializer: Database '" + dbName + "' created successfully.");
            } catch (SQLException e) {
                System.err.println("DatabaseInitializer: Error creating database '" + dbName + "': " + e.getMessage());
            }
        }
    }
}
