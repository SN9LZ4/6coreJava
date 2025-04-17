package com.cinema.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dbtest {
    
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            Connection conn = DatabaseConnection.getConnection();
            
            if (conn != null) {
                System.out.println("✅ Connected to the database successfully!");
                
                // Test querying the films table
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, titre FROM films");
                
                System.out.println("\nFilms in database:");
                boolean hasFilms = false;
                
                while (rs.next()) {
                    hasFilms = true;
                    int id = rs.getInt("id");
                    String title = rs.getString("titre");
                    System.out.println(" - Film #" + id + ": " + title);
                }
                
                if (!hasFilms) {
                    System.out.println("No films found in the database.");
                }
                
                // Test querying the salle table
                rs = stmt.executeQuery("SELECT id, nom_salle FROM salle");
                
                System.out.println("\nSalles in database:");
                boolean hasSalles = false;
                
                while (rs.next()) {
                    hasSalles = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("nom_salle");
                    System.out.println(" - Salle #" + id + ": " + name);
                }
                
                if (!hasSalles) {
                    System.out.println("No salles found in the database.");
                }
                
                // Close resources
                rs.close();
                stmt.close();
                DatabaseConnection.closeConnection();
                
            } else {
                System.out.println("❌ Failed to connect to the database.");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
