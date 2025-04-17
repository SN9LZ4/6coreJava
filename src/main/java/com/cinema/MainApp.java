package com.cinema;

import com.cinema.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // Set up the database tables if they don't exist (but don't add any data)
        try {
            System.out.println("Setting up database tables if needed...");
            DatabaseConnection.setupDatabase();
            System.out.println("Database setup complete");
        } catch (Exception e) {
            System.err.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }
        
        Parent root = FXMLLoader.load(getClass().getResource("/com/cinema/front.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cinema/styles/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/com/cinema/styles/front.css").toExternalForm());
        
        stage.setTitle("Cinema Reservation System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
