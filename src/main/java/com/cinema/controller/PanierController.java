package com.cinema.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.cinema.util.DatabaseConnection;
import com.cinema.model.Reservation;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class PanierController {
    @FXML private TableView<Reservation> cartTable;
    @FXML private TableColumn<Reservation, String> filmColumn;
    @FXML private TableColumn<Reservation, String> salleColumn;
    @FXML private TableColumn<Reservation, LocalDate> dateColumn;
    @FXML private TableColumn<Reservation, Integer> placesColumn;
    @FXML private TableColumn<Reservation, String> typeColumn;
    @FXML private TableColumn<Reservation, Double> prixColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    @FXML private Label totalPriceLabel;

    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        loadReservations();
    }

    private void setupTableColumns() {
        filmColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        salleColumn.setCellValueFactory(new PropertyValueFactory<>("salleNumero"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        placesColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typePlace"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Add action buttons column
        actionsColumn.setPrefWidth(200);
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            private final Button payBtn = new Button("Pay");
            private final HBox pane = new HBox(5);

            {
                deleteBtn.getStyleClass().add("delete-btn");
                payBtn.getStyleClass().add("search-btn");
                pane.setAlignment(Pos.CENTER);
                pane.getChildren().addAll(deleteBtn, payBtn);

                deleteBtn.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    deleteReservation(reservation);
                });

                payBtn.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    proceedToPayment(reservation, event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    public void loadReservations() {
        reservations.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT r.*, f.titre as film_titre, s.nom_salle as salle_nom " +
                "FROM reservation r " +
                "JOIN film f ON r.film_id = f.id " +
                "JOIN salle s ON r.salle_id = s.id " +
                "ORDER BY r.date_reservation DESC")) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setFilmId(rs.getInt("film_id"));
                reservation.setSalleId(rs.getInt("salle_id"));
                reservation.setDateReservation(rs.getDate("date_reservation").toLocalDate());
                reservation.setTypePlace(rs.getString("type_place"));
                reservation.setNombrePlaces(rs.getInt("nombre_places"));
                reservation.setTitre(rs.getString("film_titre"));
                reservation.setSalleNumero(rs.getString("salle_nom"));
                // Calculate price based on type and number of places
                double basePrice = "VIP".equals(rs.getString("type_place")) ? 25.0 : 15.0;
                double totalPrice = basePrice * rs.getInt("nombre_places");
                reservation.setPrix(totalPrice);
                
                reservations.add(reservation);
            }
            cartTable.setItems(reservations);
            updateTotalPrice();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reservations: " + e.getMessage());
        }
    }

    private void deleteReservation(Reservation reservation) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservation WHERE id = ?")) {
            
            stmt.setInt(1, reservation.getId());
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                reservations.remove(reservation);
                updateTotalPrice();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation deleted successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reservation");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reservation: " + e.getMessage());
        }
    }

    private void proceedToPayment(Reservation reservation, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/payment.fxml"));
            Parent root = loader.load();
            PaymentController paymentController = loader.getController();
            paymentController.setReservation(reservation);
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open payment view: " + e.getMessage());
        }
    }

    private void updateTotalPrice() {
        double total = reservations.stream()
                .mapToDouble(r -> {
                    double basePrice = "VIP".equals(r.getTypePlace()) ? 25.0 : 15.0;
                    return basePrice * r.getNombrePlaces();
                })
                .sum();
        totalPriceLabel.setText(String.format("%.2f DT", total));
    }

    @FXML
    private void handleHomeClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/front.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            // Do NOT add stylesheet here, it is already included in front.fxml
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to home: " + e.getMessage());
        }
    }

    @FXML
    private void handleReservationClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cinema/reservation.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to reservation: " + e.getMessage());
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
