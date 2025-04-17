package com.cinema.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class FrontController {
    
    @FXML
    private void handleHomeClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cinema/front.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to home: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMoviesClick() {
        showAlert(Alert.AlertType.INFORMATION, "Movies", "Movie listings feature is coming soon!");
    }
    
    @FXML
    private void handleReservationClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/reservation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to reservation: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAccountClick() {
        showAlert(Alert.AlertType.INFORMATION, "Account", "Account management feature is coming soon!");
    }
    
    @FXML
    private void handleScheduleClick() {
        showAlert(Alert.AlertType.INFORMATION, "Schedule", "Movie schedule feature is coming soon!");
    }
    
    @FXML
    public void pageupdateFront(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Profile", "Profile update feature is coming soon!");
    }

    @FXML
    public void showPanier(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cinema/panier.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load cart view");
        }
    }
    
    @FXML
    private void handlePanierClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cinema/panier.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to cart: " + e.getMessage());
        }
    }
    
    @FXML
    public void pageassociation(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Association", "Association feature is coming soon!");
    }
    
    @FXML
    public void pageReclamation(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Reclamations", "Reclamations feature is coming soon!");
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
