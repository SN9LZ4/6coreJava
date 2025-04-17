package com.cinema.controller;

import com.cinema.model.Reservation;
import com.cinema.util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;

public class PaymentController {
    @FXML private Label filmLabel;
    @FXML private Label dateLabel;
    @FXML private Label seatsLabel;
    @FXML private Label typeLabel;
    @FXML private Label amountLabel;
    @FXML private Label seatsCountLabel;
    @FXML private Label salleLabel;
    @FXML private VBox paymentForm;
    @FXML private HBox actionButtons;
    @FXML private TextField cardholderField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private PasswordField cvvField;
    
    private Reservation reservation;
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        updateLabels();
        if (amountLabel != null && reservation != null) {
            amountLabel.setText(String.format("%.2f DT", reservation.getPrix()));
        }
        // Set salle name if available
        if (salleLabel != null && reservation != null) {
            salleLabel.setText(reservation.getSalleNumero() != null ? reservation.getSalleNumero() : "");
        }
    }
    
    private void updateLabels() {
        if (reservation != null) {
            filmLabel.setText(reservation.getTitre());
            dateLabel.setText(reservation.getDateReservation() != null ? reservation.getDateReservation().toString() : "");
            seatsLabel.setText(reservation.getSelectedSeats());
            typeLabel.setText(reservation.getTypePlace());
            if (seatsCountLabel != null) {
                seatsCountLabel.setText(String.valueOf(reservation.getNombrePlaces()));
            }
            if (salleLabel != null) {
                salleLabel.setText(reservation.getSalleNumero() != null ? reservation.getSalleNumero() : "");
            }
            // Use the prix field directly for amount
            amountLabel.setText(String.format("%.2f DT", reservation.getPrix()));
        }
    }
    
    @FXML
    public void initialize() {
        if (paymentForm != null) paymentForm.setVisible(false);
        if (actionButtons != null) actionButtons.setVisible(true);
        // Ensure payment form is not managed when hidden (prevents layout space)
        if (paymentForm != null) paymentForm.setManaged(false);
        if (actionButtons != null) actionButtons.setManaged(true);
    }
    
    @FXML
    private void showPaymentForm() {
        if (paymentForm != null) {
            paymentForm.setVisible(true);
            paymentForm.setManaged(true);
        }
        if (actionButtons != null) {
            actionButtons.setVisible(false);
            actionButtons.setManaged(false);
        }
    }

    @FXML
    private void handleCancelPayment() {
        if (paymentForm != null) {
            paymentForm.setVisible(false);
            paymentForm.setManaged(false);
        }
        if (actionButtons != null) {
            actionButtons.setVisible(true);
            actionButtons.setManaged(true);
        }
    }

    @FXML
    private void handleFinalPayment(ActionEvent event) {
        // Basic validation (expand as needed)
        if (cardholderField.getText().isEmpty() || cardNumberField.getText().isEmpty() || expiryField.getText().isEmpty() || cvvField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all payment details.");
            return;
        }
        // Simulate payment processing (add real logic if needed)
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE reservation SET status = 'Paid' WHERE id = ?")) {
            stmt.setInt(1, reservation.getId());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment processed successfully!\nThank you for your purchase.");
                goBack(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to process payment");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process payment: " + e.getMessage());
        }
    }
    
    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/front.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cinema/styles/style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/com/cinema/styles/front.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate to home page");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
