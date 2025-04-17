package Controller;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;

public class AdminDashboard extends Application implements Initializable {

    @FXML
    private void ouvrirAfficherArticle() {
        System.out.println("Actualites clicked");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des articles");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("✅ Page AfficherArticle.fxml ouverte !");
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("❌ Fichier FXML introuvable. Chemin incorrect ou fichier absent.");
            e.printStackTrace();
        }
    }

    // UI components
    @FXML private TextField searchField;
    @FXML private ImageView logoImage;
    @FXML private Button btnDashboard, btnUsers, btnReclamations, btnActualites, btnFinance,
            btnProjections, btnAssociations, btnProfile, btnLogout,
            btnSettings, btnNotif;

    // Constants
    private static final String DARK_BLUE = "#1a237e";
    private static final String GOLD = "#ffd700";
    private static final String LIGHT_GRAY = "#f5f5f5";

    // Utility fields
    private final Random random = new Random();
    private ScheduledExecutorService executorService;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupLogoEffects();
        initializeStatistics();
        initializeTables();
        setupSearchField();
        setupEventHandlers();
        startClock();
        initializeCharts();
        startDataUpdates();
    }

    private void startDataUpdates() {
        // Logic to update charts/statistics dynamically
    }

    private void initializeCharts() {
        // Initialize charts like PieChart, LineChart, etc.
    }

    private void startClock() {
        // Implement clock display updates
    }

    private void initializeTables() {
        // Populate tables if any
    }

    private void initializeStatistics() {
        // Load and show statistical values
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            AnchorPane root = loader.load(); // FXML file must have fx:controller set
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

            primaryStage.setTitle("Chouflifilm - Dashboard Administrateur");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearchField() {
        if (searchField != null) {
            searchField.setPromptText("Rechercher...");
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("Searching: " + newValue);
            });
        }
    }

    private void setupLogoEffects() {
        if (logoImage != null) {
            addHoverEffect(logoImage);
        }
    }

    private void addHoverEffect(ImageView image) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), image);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), image);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        image.setOnMouseEntered(e -> scaleIn.playFromStart());
        image.setOnMouseExited(e -> scaleOut.playFromStart());
    }

    private void setupEventHandlers() {
        if (btnDashboard != null) btnDashboard.setOnAction(e -> handleDashboard());
        if (btnUsers != null) btnUsers.setOnAction(e -> handleUsers());
        if (btnReclamations != null) btnReclamations.setOnAction(e -> handleReclamations());
        if (btnActualites != null) btnActualites.setOnAction(e -> handleActualites());
        if (btnFinance != null) btnFinance.setOnAction(e -> handleFinance());
        if (btnProjections != null) btnProjections.setOnAction(e -> handleProjections());
        if (btnAssociations != null) btnAssociations.setOnAction(e -> handleAssociations());
        if (btnProfile != null) btnProfile.setOnAction(e -> handleProfile());
        if (btnLogout != null) btnLogout.setOnAction(e -> handleLogout());
        if (btnSettings != null) btnSettings.setOnAction(e -> handleSettings());
        if (btnNotif != null) btnNotif.setOnAction(e -> handleNotifications());
    }

    private void handleDashboard() {
        System.out.println("Dashboard clicked");
    }

    private void handleUsers() {
        System.out.println("Users clicked");
    }

    private void handleReclamations() {
        System.out.println("Reclamations clicked");
    }

    private void handleActualites() {
        System.out.println("Actualites clicked");
        ouvrirAfficherArticle(); // ✅ Ouvre la page AfficherArticle.fxml
    }


    private void handleFinance() {
        System.out.println("Finance clicked");
    }

    private void handleProjections() {
        System.out.println("Projections clicked");
    }

    private void handleAssociations() {
        System.out.println("Associations clicked");
    }

    private void handleProfile() {
        System.out.println("Profile clicked");
    }

    private void handleLogout() {
        Platform.exit();
    }

    private void handleSettings() {
        System.out.println("Settings clicked");
    }

    private void handleNotifications() {
        System.out.println("Notifications clicked");
    }

    public static void main(String[] args) {
        launch(args);
    }



}
