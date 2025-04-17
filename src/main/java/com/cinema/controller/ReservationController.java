package com.cinema.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.LinkedHashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox; // Added import
import javafx.stage.Stage;
import com.cinema.model.Reservation;
import com.cinema.util.DatabaseConnection;

public class ReservationController implements Initializable {
    @FXML private TextField titreField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private Spinner<Integer> nombrePlacesSpinner;
    @FXML private ComboBox<String> filmCombo;
    @FXML private ComboBox<String> salleCombo;
    @FXML private ComboBox<String> associationCombo;
    @FXML private ComboBox<String> typePlaceCombo;
    @FXML private Button confirmButton;
    @FXML private Button selectSeatsButton; // New button for seat popup
    @FXML private GridPane seatsGrid;
    @FXML private Label selectedSeatsLabel;

    private LocalDate filmStartDate;
    private LocalDate filmEndDate;
    private final Set<Integer> selectedSeatIds = new LinkedHashSet<>();
    private final List<Button> seatButtons = new ArrayList<>();
    private int salleNbrPlaces = 64; // Default, can be set dynamically
    private int nbrSeatsToSelect = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes
        typePlaceCombo.setItems(FXCollections.observableArrayList("Standard", "VIP"));
        typePlaceCombo.setValue("Standard");
        
        // Initialize Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        nombrePlacesSpinner.setValueFactory(valueFactory);

        // Set up date picker validation
        dateReservationPicker.setDayCellFactory(new javafx.util.Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (empty || date == null) {
                            setDisable(true);
                        } else {
                            // Disable dates outside the film's screening period
                            boolean isValidDate = (filmStartDate == null || !date.isBefore(filmStartDate)) &&
                                               (filmEndDate == null || !date.isAfter(filmEndDate));
                            setDisable(!isValidDate);
                            setStyle(isValidDate ? "" : "-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        });

        // Load data
        loadFilms();
        loadAssociations();
        
        // Add listener for film selection
        filmCombo.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    loadSallesForFilm(newValue);
                    updateFilmDates(newValue);
                } else {
                    // Reset dates when no film is selected
                    filmStartDate = null;
                    filmEndDate = null;
                    dateReservationPicker.setValue(null);
                }
            }
        });

        // Set up seat selection
        salleNbrPlaces = 64; // You can set this dynamically from salle selection
        nbrSeatsToSelect = nombrePlacesSpinner.getValue();
        buildSeats();
        updateSelectedSeatsLabel();
        confirmButton.setDisable(true);
        nombrePlacesSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            nbrSeatsToSelect = newVal;
            buildSeats();
            updateSelectedSeatsLabel();
            confirmButton.setDisable(true);
        });
    }

    @FXML
    private void handleConfirm() {
        if (selectedSeatIds.size() != nbrSeatsToSelect) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select the required number of seats.");
            return;
        }
        // Create a new reservation object
        Reservation reservation = new Reservation();
        reservation.setTitre(filmCombo.getValue());
        reservation.setDateReservation(dateReservationPicker.getValue());
        reservation.setTypePlace(typePlaceCombo.getValue());
        reservation.setNombrePlaces(nombrePlacesSpinner.getValue());
        reservation.setFilmId(getFilmId(filmCombo.getValue()));
        reservation.setSalleId(getSalleId(salleCombo.getValue()));
        reservation.setStatus("Confirmed");
        reservation.setSelectedSeats(selectedSeatIds.toString());
        reservation.setSalleNumero(getSalleName(salleCombo.getValue()));
        // Set price using model logic
        reservation.setPrix(("VIP".equals(typePlaceCombo.getValue()) ? 25.0 : 15.0) * nombrePlacesSpinner.getValue());
        // Save the reservation first
        boolean saved = saveReservation(reservation);
        if (saved) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation saved successfully!");
            // Update cart (Panier) UI
            updateCartWithReservation(reservation);
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save reservation.");
        }
    }

    private boolean saveReservation(Reservation reservation) {
        // Log all values before inserting
        System.out.println("Attempting to save reservation:");
        System.out.println("Film ID: " + reservation.getFilmId());
        System.out.println("Salle ID: " + reservation.getSalleId());
        System.out.println("Titre: " + reservation.getTitre());
        System.out.println("Date: " + reservation.getDateReservation());
        System.out.println("Nombre Places: " + reservation.getNombrePlaces());
        System.out.println("Type Place: " + reservation.getTypePlace());
        System.out.println("Status: " + reservation.getStatus());
        System.out.println("Selected Seats: " + reservation.getSelectedSeats());
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO reservation (film_id, salle_id, association_id, user_id, titre, date_reservation, nombre_places, type_place, status, selected_seats, prix) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, reservation.getFilmId());
                pstmt.setInt(2, reservation.getSalleId());
                pstmt.setNull(3, java.sql.Types.INTEGER); // No association ID for now
                pstmt.setInt(4, 1); // Default user_id for now
                pstmt.setString(5, reservation.getTitre()); 
                pstmt.setDate(6, java.sql.Date.valueOf(reservation.getDateReservation()));
                pstmt.setInt(7, reservation.getNombrePlaces());
                pstmt.setString(8, reservation.getTypePlace());
                pstmt.setString(9, reservation.getStatus());
                pstmt.setString(10, reservation.getSelectedSeats());
                pstmt.setDouble(11, reservation.getPrix());
                pstmt.executeUpdate();
                System.out.println("Reservation inserted successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception while saving reservation: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error saving reservation: " + e.getMessage());
            return false;
        }
    }

    private void updateCartWithReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/panier.fxml"));
            Parent root = loader.load();
            // Make sure the cart loads the latest reservations
            PanierController panierController = loader.getController();
            panierController.loadReservations();
            // Redirect to the cart in the same window
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open cart: " + e.getMessage());
        }
    }

    @FXML
    private void viewPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/panier.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cinema/styles/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open cart view");
        }
    }

    @FXML
    private void handlePayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/payment.fxml"));
            Parent root = loader.load();
            // Create a new reservation object
            Reservation reservation = new Reservation();
            reservation.setTitre(filmCombo.getValue());
            reservation.setDateReservation(dateReservationPicker.getValue());
            reservation.setTypePlace(typePlaceCombo.getValue());
            reservation.setNombrePlaces(nombrePlacesSpinner.getValue());
            reservation.setFilmId(getFilmId(filmCombo.getValue()));
            reservation.setSalleId(getSalleId(salleCombo.getValue()));
            reservation.setStatus("Pending");
            reservation.setSelectedSeats(selectedSeatIds.toString());
            reservation.setSalleNumero(getSalleName(salleCombo.getValue()));
            // Set price using model logic
            reservation.setPrix(("VIP".equals(typePlaceCombo.getValue()) ? 25.0 : 15.0) * nombrePlacesSpinner.getValue());
            // Save the reservation first
            saveReservation(reservation);
            // Get the controller and set reservation
            PaymentController paymentController = loader.getController();
            paymentController.setReservation(reservation);
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cinema/styles/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open payment view");
        }
    }
    
    private int getFilmId(String filmTitle) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM film WHERE titre = ?");
            stmt.setString(1, filmTitle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default to ID 1 if not found
    }
    
    private int getSalleId(String salleValue) {
        if (salleValue != null && salleValue.contains(":")) {
            return Integer.parseInt(salleValue.split(":")[0]);
        }
        return 1; // Default to ID 1 if not found
    }

    private String getSalleName(String salleValue) {
        // salleValue format is "ID:Name"
        if (salleValue != null && salleValue.contains(":")) {
            return salleValue.split(":", 2)[1].trim();
        }
        return "";
    }

    private void loadFilms() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT titre FROM film ORDER BY titre");
            ResultSet rs = stmt.executeQuery();
            
            ObservableList<String> films = FXCollections.observableArrayList();
            while (rs.next()) {
                films.add(rs.getString("titre"));
            }
            filmCombo.setItems(films);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading films: " + e.getMessage());
        }
    }

    private void updateFilmDates(String filmTitle) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT date_debut, date_fin FROM film WHERE titre = ?");
            pstmt.setString(1, filmTitle);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                filmStartDate = rs.getDate("date_debut").toLocalDate();
                filmEndDate = rs.getDate("date_fin").toLocalDate();
                // Reset date picker to force validation
                dateReservationPicker.setValue(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load film dates");
        }
    }

    private void loadSallesForFilm(String filmTitle) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Get salles that are linked to the selected film through salle_film table
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT s.id, s.* FROM salle s " +
                "INNER JOIN salle_film sf ON s.id = sf.salle_id " +
                "INNER JOIN film f ON f.id = sf.film_id " +
                "WHERE f.titre = ?");
            pstmt.setString(1, filmTitle);
            ResultSet rs = pstmt.executeQuery();
            
            ObservableList<String> salles = FXCollections.observableArrayList();
            while (rs.next()) {
                // Store ID and name in the format "ID:name" for easy retrieval later
                salles.add(rs.getInt("id") + ":" + rs.getString(3));
            }
            
            if (salles.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Theaters Available", 
                         "No theaters are currently showing the movie: " + filmTitle);
            }
            
            salleCombo.setItems(salles);
            salleCombo.setValue(null); // Reset selection
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading salles: " + e.getMessage());
        }
    }

    private void clearForm() {
        filmCombo.setValue(null);
        salleCombo.setValue(null);
        associationCombo.setValue(null);
        dateReservationPicker.setValue(null);
        nombrePlacesSpinner.getValueFactory().setValue(1);
        typePlaceCombo.setValue("Standard");
    }

    /**
     * Load associations from the database
     */
    private void loadAssociations() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nom FROM association ORDER BY nom")) {
            
            ObservableList<String> associations = FXCollections.observableArrayList();
            associations.add(""); // Add empty option for no association
            
            while (rs.next()) {
                associations.add(rs.getString("nom")); // Use 'nom' column for association name
            }
            
            associationCombo.setItems(associations);
            associationCombo.setValue(""); // Set default to no association
            
            if (associations.size() <= 1) { // Only empty option
                System.out.println("No associations found in the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading associations: " + e.getMessage());
        }
    }

    private void buildSeats() {
        seatsGrid.getChildren().clear();
        seatButtons.clear();
        selectedSeatIds.clear();
        int columns = 8;
        int rows = (int) Math.ceil((double) salleNbrPlaces / columns);
        int seatId = 1;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (seatId > salleNbrPlaces) break;
                Button seatBtn = new Button(String.valueOf(seatId));
                seatBtn.setMinSize(36, 36);
                seatBtn.setStyle("-fx-background-radius: 18; -fx-background-color: #e0e0e0; -fx-border-color: #aaa; -fx-font-weight: bold;");
                final int id = seatId;
                seatBtn.setOnMouseClicked(e -> handleSeatClick(id, seatBtn));
                seatButtons.add(seatBtn);
                seatsGrid.add(seatBtn, col, row);
                seatId++;
            }
        }
        confirmButton.setDisable(true);
    }

    private void handleSeatClick(int seatId, Button btn) {
        if (selectedSeatIds.contains(seatId)) {
            selectedSeatIds.remove(seatId);
            btn.setStyle("-fx-background-radius: 18; -fx-background-color: #e0e0e0; -fx-border-color: #aaa; -fx-font-weight: bold;");
        } else {
            if (selectedSeatIds.size() >= nbrSeatsToSelect) return;
            selectedSeatIds.add(seatId);
            btn.setStyle("-fx-background-radius: 18; -fx-background-color: #4CAF50; -fx-border-color: #388E3C; -fx-text-fill: white; -fx-font-weight: bold;");
        }
        updateSelectedSeatsLabel();
        confirmButton.setDisable(selectedSeatIds.size() != nbrSeatsToSelect);
    }

    @FXML
    private void handleSelectSeats() {
        // Create a new Stage for the seat selection popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Your Seats");
        // Layout for seat selection
        GridPane seatLayout = new GridPane();
        seatLayout.setHgap(8);
        seatLayout.setVgap(8);
        seatLayout.setStyle("-fx-padding: 30; -fx-background-color: #222; -fx-border-radius: 10; -fx-background-radius: 10;");
        // Add a label for the screen
        Label screenLabel = new Label("SCREEN");
        screenLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fff; -fx-background-color: #444; -fx-padding: 10 0 10 0; -fx-alignment: center;");
        seatLayout.add(screenLabel, 0, 0, 8, 1); // Assume 8 seats per row
        // Generate seat buttons
        List<Button> popupSeatButtons = new ArrayList<>();
        Set<Integer> tempSelectedSeats = new LinkedHashSet<>(selectedSeatIds); // Copy current selection
        int seatsPerRow = 8;
        int totalSeats = salleNbrPlaces;
        int rowCount = (int) Math.ceil((double) totalSeats / seatsPerRow);
        int seatNum = 1;
        for (int row = 1; row <= rowCount; row++) {
            for (int col = 0; col < seatsPerRow && seatNum <= totalSeats; col++, seatNum++) {
                Button seatBtn = new Button(String.valueOf(seatNum));
                seatBtn.setPrefSize(40, 40);
                seatBtn.setStyle("-fx-background-radius: 8; -fx-background-color: #e2b616; -fx-font-weight: bold;");
                if (tempSelectedSeats.contains(seatNum)) {
                    seatBtn.setStyle("-fx-background-radius: 8; -fx-background-color: #4caf50; -fx-font-weight: bold; -fx-text-fill: #fff;");
                }
                final int seatId = seatNum;
                seatBtn.setOnAction(e -> {
                    if (tempSelectedSeats.contains(seatId)) {
                        tempSelectedSeats.remove(seatId);
                        seatBtn.setStyle("-fx-background-radius: 8; -fx-background-color: #e2b616; -fx-font-weight: bold;");
                    } else {
                        if (tempSelectedSeats.size() < nbrSeatsToSelect) {
                            tempSelectedSeats.add(seatId);
                            seatBtn.setStyle("-fx-background-radius: 8; -fx-background-color: #4caf50; -fx-font-weight: bold; -fx-text-fill: #fff;");
                        }
                    }
                });
                popupSeatButtons.add(seatBtn);
                seatLayout.add(seatBtn, col, row);
            }
        }
        // Confirm button
        Button confirmBtn = new Button("Confirm Selection");
        confirmBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: #fff; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20;");
        confirmBtn.setOnAction(e -> {
            if (tempSelectedSeats.size() != nbrSeatsToSelect) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select exactly " + nbrSeatsToSelect + " seats.", ButtonType.OK);
                alert.showAndWait();
            } else {
                selectedSeatIds.clear();
                selectedSeatIds.addAll(tempSelectedSeats);
                updateSelectedSeatsLabel();
                popupStage.close();
            }
        });
        VBox popupRoot = new VBox(20, seatLayout, confirmBtn);
        popupRoot.setStyle("-fx-padding: 30; -fx-background-color: #222;");
        popupRoot.setAlignment(javafx.geometry.Pos.CENTER);
        Scene popupScene = new Scene(popupRoot);
        popupStage.setScene(popupScene);
        popupStage.initOwner(selectSeatsButton.getScene().getWindow());
        popupStage.showAndWait();
    }

    private void updateSelectedSeatsLabel() {
        if (selectedSeatsLabel != null) {
            selectedSeatsLabel.setText("Selected seats: " + selectedSeatIds);
        }
        confirmButton.setDisable(selectedSeatIds.size() != nbrSeatsToSelect);
    }

    @FXML
    private void handlePanierClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/panier.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to cart view: " + e.getMessage());
        }
    }

    @FXML
    private void handleHomeClick(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cinema/front.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cinema/styles/front.css").toExternalForm());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to home: " + e.getMessage());
        }
    }

    @FXML
    private void handleMenuClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinema/front.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Error navigating to menu: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
