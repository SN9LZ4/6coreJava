package com.cinema.util;

import java.sql.*;

public class DatabaseInspector {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get all tables except phpMyAdmin tables
            try (Statement stmt = conn.createStatement();
                 ResultSet tables = stmt.executeQuery("SHOW TABLES")) {
                
                while (tables.next()) {
                    String tableName = tables.getString(1);
                    
                    // Skip phpMyAdmin tables
                    if (tableName.startsWith("pma__")) {
                        continue;
                    }
                    
                    System.out.println("\nTable: " + tableName);
                    System.out.println("------------------------");
                    
                    // Show table structure
                    try (Statement descStmt = conn.createStatement();
                         ResultSet structure = descStmt.executeQuery("DESCRIBE " + tableName)) {
                        
                        System.out.printf("%-20s %-20s %-10s %-10s%n", "Field", "Type", "Null", "Key");
                        System.out.println("------------------------------------------------------------");
                        
                        while (structure.next()) {
                            System.out.printf("%-20s %-20s %-10s %-10s%n",
                                structure.getString("Field"),
                                structure.getString("Type"),
                                structure.getString("Null"),
                                structure.getString("Key"));
                        }
                    }
                    
                    // Show row count
                    try (Statement countStmt = conn.createStatement();
                         ResultSet countRs = countStmt.executeQuery("SELECT COUNT(*) as count FROM " + tableName)) {
                        if (countRs.next()) {
                            System.out.println("\nTotal rows: " + countRs.getInt("count"));
                        }
                    }
                    
                    // Show a sample row if table is not empty
                    try (Statement sampleStmt = conn.createStatement();
                         ResultSet sampleRs = sampleStmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
                        
                        if (sampleRs.next()) {
                            System.out.println("\nSample Data:");
                            ResultSetMetaData rsmd = sampleRs.getMetaData();
                            int columnCount = rsmd.getColumnCount();
                            
                            for (int i = 1; i <= columnCount; i++) {
                                String columnName = rsmd.getColumnName(i);
                                String value = sampleRs.getString(i);
                                if (value != null) {
                                    System.out.printf("%s: %s%n", columnName, value);
                                }
                            }
                        }
                    }
                    
                    System.out.println("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
