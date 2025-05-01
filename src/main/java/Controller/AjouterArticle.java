package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.article;
import tn.esprit.services.ArticleService;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AjouterArticle {

    @FXML private TextField nomField;
    @FXML private ComboBox<String> categorieField;
    @FXML private DatePicker dateField;
    @FXML private TextArea contenuField;
    @FXML private TextField imageArticleField;
    @FXML private ImageView articleImageView;
    @FXML private Button btnAjouter;
    
    // Labels d'erreur pour chaque champ
    @FXML private Label nomErrorLabel;
    @FXML private Label categorieErrorLabel;
    @FXML private Label dateErrorLabel;
    @FXML private Label contenuErrorLabel;
    @FXML private Label imageErrorLabel;

    private File selectedImageFile;
    private final ArticleService articleService = new ArticleService();
    private final Map<String, Boolean> validationStatus = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialiser les catégories
        categorieField.getItems().addAll("film", "auteur", "cinema", "snacks", "autre");
        dateField.setValue(LocalDate.now());
        
        // Initialiser le statut de validation
        validationStatus.put("nom", false);
        validationStatus.put("categorie", false);
        validationStatus.put("date", true); // Date est pré-remplie
        validationStatus.put("contenu", false);
        validationStatus.put("image", false);
        
        // Ajouter les écouteurs pour la validation en temps réel
        setupValidationListeners();
        
        // Désactiver le bouton d'ajout initialement
        updateButtonState();
    }
    
    private void setupValidationListeners() {
        // Validation du titre
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNom(newValue);
        });
        
        // Validation de la catégorie
        categorieField.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateCategorie(newValue);
        });
        
        // Validation de la date
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateDate(newValue);
        });
        
        // Validation du contenu
        contenuField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateContenu(newValue);
        });
        
        // Validation de l'image (mise à jour dans handleImageUpload)
    }
    
    private void validateNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            nomErrorLabel.setText("Le titre est requis");
            nomErrorLabel.setStyle("-fx-text-fill: red;");
            nomField.setStyle("-fx-border-color: red;");
            validationStatus.put("nom", false);
        } else if (nom.length() < 3) {
            nomErrorLabel.setText("Le titre doit contenir au moins 3 caractères");
            nomErrorLabel.setStyle("-fx-text-fill: red;");
            nomField.setStyle("-fx-border-color: red;");
            validationStatus.put("nom", false);
        } else if (!nom.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,100}$")) {
            nomErrorLabel.setText("Le titre doit contenir uniquement des lettres");
            nomErrorLabel.setStyle("-fx-text-fill: red;");
            nomField.setStyle("-fx-border-color: red;");
            validationStatus.put("nom", false);
        } else {
            nomErrorLabel.setText("✓");
            nomErrorLabel.setStyle("-fx-text-fill: green;");
            nomField.setStyle("-fx-border-color: green;");
            validationStatus.put("nom", true);
        }
        updateButtonState();
    }
    
    private void validateCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            categorieErrorLabel.setText("La catégorie est requise");
            categorieErrorLabel.setStyle("-fx-text-fill: red;");
            categorieField.setStyle("-fx-border-color: red;");
            validationStatus.put("categorie", false);
        } else {
            categorieErrorLabel.setText("✓");
            categorieErrorLabel.setStyle("-fx-text-fill: green;");
            categorieField.setStyle("-fx-border-color: green;");
            validationStatus.put("categorie", true);
        }
        updateButtonState();
    }
    
    private void validateDate(LocalDate date) {
        if (date == null) {
            dateErrorLabel.setText("La date est requise");
            dateErrorLabel.setStyle("-fx-text-fill: red;");
            dateField.setStyle("-fx-border-color: red;");
            validationStatus.put("date", false);
        } else if (!date.equals(LocalDate.now())) {
            dateErrorLabel.setText("La date doit être celle d'aujourd'hui");
            dateErrorLabel.setStyle("-fx-text-fill: red;");
            dateField.setStyle("-fx-border-color: red;");
            validationStatus.put("date", false);
        } else {
            dateErrorLabel.setText("✓");
            dateErrorLabel.setStyle("-fx-text-fill: green;");
            dateField.setStyle("-fx-border-color: green;");
            validationStatus.put("date", true);
        }
        updateButtonState();
    }
    
    private void validateContenu(String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            contenuErrorLabel.setText("Le contenu est requis");
            contenuErrorLabel.setStyle("-fx-text-fill: red;");
            contenuField.setStyle("-fx-border-color: red;");
            validationStatus.put("contenu", false);
        } else if (contenu.length() < 10) {
            contenuErrorLabel.setText("Le contenu doit contenir au moins 10 caractères");
            contenuErrorLabel.setStyle("-fx-text-fill: red;");
            contenuField.setStyle("-fx-border-color: red;");
            validationStatus.put("contenu", false);
        } else if (!contenu.matches(".*[a-zA-ZÀ-ÿ].*")) {
            contenuErrorLabel.setText("Le contenu doit contenir des lettres");
            contenuErrorLabel.setStyle("-fx-text-fill: red;");
            contenuField.setStyle("-fx-border-color: red;");
            validationStatus.put("contenu", false);
        } else {
            contenuErrorLabel.setText("✓");
            contenuErrorLabel.setStyle("-fx-text-fill: green;");
            contenuField.setStyle("-fx-border-color: green;");
            validationStatus.put("contenu", true);
        }
        updateButtonState();
    }
    
    private void validateImage() {
        if (selectedImageFile == null) {
            imageErrorLabel.setText("Une image est requise");
            imageErrorLabel.setStyle("-fx-text-fill: red;");
            imageArticleField.setStyle("-fx-border-color: red;");
            validationStatus.put("image", false);
        } else {
            String fileName = selectedImageFile.getName().toLowerCase();
            if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png")) {
                imageErrorLabel.setText("Format d'image invalide (jpg, jpeg, png uniquement)");
                imageErrorLabel.setStyle("-fx-text-fill: red;");
                imageArticleField.setStyle("-fx-border-color: red;");
                validationStatus.put("image", false);
            } else {
                imageErrorLabel.setText("✓");
                imageErrorLabel.setStyle("-fx-text-fill: green;");
                imageArticleField.setStyle("-fx-border-color: green;");
                validationStatus.put("image", true);
            }
        }
        updateButtonState();
    }
    
    private void updateButtonState() {
        // Activer le bouton uniquement si tous les champs sont valides
        boolean allValid = validationStatus.values().stream().allMatch(valid -> valid);
        btnAjouter.setDisable(!allValid);
        
        if (allValid) {
            btnAjouter.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10.0 25.0; -fx-background-radius: 5.0; -fx-font-weight: bold; -fx-font-size: 16.0; -fx-cursor: hand;");
        } else {
            btnAjouter.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-padding: 10.0 25.0; -fx-background-radius: 5.0; -fx-font-weight: bold; -fx-font-size: 16.0;");
        }
    }

    @FXML
    void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String imageName = System.currentTimeMillis() + "_" + file.getName();
                File destDir = new File("src/main/resources/images");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, imageName);
                java.nio.file.Files.copy(file.toPath(), destFile.toPath());

                selectedImageFile = destFile;
                imageArticleField.setText(imageName);
                articleImageView.setImage(new Image(destFile.toURI().toString()));

                // Valider l'image après le téléchargement
                validateImage();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'image.");
                imageErrorLabel.setText("Erreur lors du chargement de l'image");
                imageErrorLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    void addArticle(ActionEvent event) {
        try {
            // Tous les champs sont déjà validés grâce aux listeners
            article newArticle = new article(
                nomField.getText().trim(),
                Date.valueOf(dateField.getValue()),
                contenuField.getText().trim(),
                selectedImageFile.getName(),
                0.0,  // rating
                categorieField.getValue(),
                0,    // rating_count
                0,    // likes
                0     // dislikes
            );

            articleService.add(newArticle);
            showAlert(Alert.AlertType.INFORMATION, "Article ajouté avec succès!");
            clearFields();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Ajouter Article");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        categorieField.setValue(null);
        dateField.setValue(LocalDate.now());
        contenuField.clear();
        imageArticleField.clear();
        articleImageView.setImage(null);
        selectedImageFile = null;
        
        // Réinitialiser les statuts de validation
        validationStatus.put("nom", false);
        validationStatus.put("categorie", false);
        validationStatus.put("date", true);
        validationStatus.put("contenu", false);
        validationStatus.put("image", false);
        
        // Réinitialiser les styles
        nomField.setStyle("");
        categorieField.setStyle("");
        dateField.setStyle("");
        contenuField.setStyle("");
        imageArticleField.setStyle("");
        
        // Effacer les messages d'erreur
        nomErrorLabel.setText("");
        categorieErrorLabel.setText("");
        dateErrorLabel.setText("");
        contenuErrorLabel.setText("");
        imageErrorLabel.setText("");
        
        // Mettre à jour l'état du bouton
        updateButtonState();
    }
}