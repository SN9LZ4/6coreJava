package com.cinema.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    // Connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/6core_cinema";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // Debug flag
    private static final boolean DEBUG = true;
    
    // For storing database connection
    private static Connection connection;

    /**
     * Get a database connection with detailed error reporting
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Force load the MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                debugLog("MySQL JDBC Driver loaded successfully");
                
                // Set connection properties with more reliable parameters
                Properties props = new Properties();
                props.setProperty("user", USER);
                props.setProperty("password", PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("allowPublicKeyRetrieval", "true");
                props.setProperty("serverTimezone", "UTC");
                props.setProperty("connectTimeout", "5000"); // 5 second timeout
                
                debugLog("Attempting to connect to " + URL + " as user '" + USER + "'");
                connection = DriverManager.getConnection(URL, props);
                
                // Print connection details for debugging
                printConnectionDetails(connection);
                
                debugLog("Connection successful!");
            } catch (ClassNotFoundException e) {
                System.err.println("ERROR: MySQL JDBC Driver not found.");
                e.printStackTrace();
                throw new SQLException("MySQL JDBC Driver not found", e);
            } catch (SQLException e) {
                System.err.println("ERROR: Could not connect to database " + URL);
                System.err.println("Error message: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error code: " + e.getErrorCode());
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Print detailed information about the database connection
     */
    private static void printConnectionDetails(Connection conn) {
        try {
            if (conn != null) {
                DatabaseMetaData metaData = conn.getMetaData();
                debugLog("Connected to: " + metaData.getURL());
                debugLog("Database product: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
                debugLog("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
                debugLog("User: " + metaData.getUserName());
                
                // Check if the database is actually 6core_cinema
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
                if (rs.next()) {
                    String currentDb = rs.getString(1);
                    if ("6core_cinema".equalsIgnoreCase(currentDb)) {
                        debugLog("✅ Successfully connected to 6core_cinema database");
                    } else {
                        debugLog("⚠️ WARNING: Connected to " + currentDb + " instead of 6core_cinema");
                    }
                }
                rs.close();
                stmt.close();
                
                // List tables to verify connection
                listTables(conn);
            }
        } catch (SQLException e) {
            System.err.println("Error getting connection details: " + e.getMessage());
        }
    }
    
    /**
     * List tables in the database to verify connection
     */
    private static void listTables(Connection conn) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            debugLog("Tables in database:");
            int tableCount = 0;
            while (tables.next()) {
                tableCount++;
                String tableName = tables.getString("TABLE_NAME");
                debugLog(" - " + tableName);
                
                // For critical tables, show row count
                if (tableName.equalsIgnoreCase("film") || 
                    tableName.equalsIgnoreCase("salle") || 
                    tableName.equalsIgnoreCase("salle_film")) {
                    showTableRowCount(conn, tableName);
                }
            }
            tables.close();
            
            if (tableCount == 0) {
                debugLog("⚠️ WARNING: No tables found in the database!");
            }
        } catch (SQLException e) {
            System.err.println("Error listing tables: " + e.getMessage());
        }
    }
    
    /**
     * Show row count for important tables
     */
    private static void showTableRowCount(Connection conn, String tableName) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if (rs.next()) {
                int count = rs.getInt(1);
                debugLog("   → Contains " + count + " rows");
                
                // If this is the films table, show actual film titles
                if (tableName.equalsIgnoreCase("film") && count > 0) {
                    ResultSet filmsRs = stmt.executeQuery("SELECT id, titre FROM film LIMIT 5");
                    debugLog("   → Film examples:");
                    while (filmsRs.next()) {
                        debugLog("      #" + filmsRs.getInt("id") + ": " + filmsRs.getString("titre"));
                    }
                    filmsRs.close();
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error checking table " + tableName + ": " + e.getMessage());
        }
    }
    
    /**
     * Setup database tables if needed
     */
    public static void setupDatabase() {
        try {
            Connection conn = getConnection();
            debugLog("Connected to database successfully");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error setting up database: " + e.getMessage());
        }
    }

    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                debugLog("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Log debug messages if debug is enabled
     */
    private static void debugLog(String message) {
        if (DEBUG) {
            System.out.println("[DB] " + message);
        }
    }
}