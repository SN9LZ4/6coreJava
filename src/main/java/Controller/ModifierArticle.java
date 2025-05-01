package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.article;
import tn.esprit.services.ArticleService;
import tn.esprit.services.ServiceException;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ModifierArticle {

    @FXML private TextField titreField;
    @FXML private ComboBox<String> categorieField;
    @FXML private DatePicker dateField;
    @FXML private TextArea contenuField;
    @FXML private TextField imageArticleField;
    @FXML private ImageView articleImageView;
    @FXML private Button btnModifier;
    
    // Labels d'erreur pour chaque champ
    @FXML private Label titreErrorLabel;
    @FXML private Label categorieErrorLabel;
    @FXML private Label dateErrorLabel;
    @FXML private Label contenuErrorLabel;
    @FXML private Label imageErrorLabel;

    private File selectedImageFile;
    private final ArticleService articleService = new ArticleService();
    private article articleAModifier;
    private final Map<String, Boolean> validationStatus = new HashMap<>();

    // Injecte l'article à modifier
    public void setArticle(article a) {
        this.articleAModifier = a;

        // Initialiser le statut de validation
        validationStatus.put("titre", true);
        validationStatus.put("categorie", true);
        validationStatus.put("date", true);
        validationStatus.put("contenu", true);
        validationStatus.put("image", true);

        // Remplir les champs avec les données de l'article
        titreField.setText(a.getTitre());
        categorieField.getItems().addAll("snacks", "auteur", "cinema", "film", "autres");
        categorieField.setValue(a.getCategorie());
        dateField.setValue(new java.sql.Date(a.getDate_publication().getTime()).toLocalDate());
        contenuField.setText(a.getContenu());

        imageArticleField.setText(a.getImage());
        File imageFile = new File("src/main/resources/images/" + a.getImage());
        if (imageFile.exists()) {
            articleImageView.setImage(new Image(imageFile.toURI().toString()));
        }

        // Empêche de choisir une date autre qu'aujourd'hui
        dateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });
        
        // Configurer les écouteurs de validation
        setupValidationListeners();
        
        // Valider les champs initiaux
        validateTitre(a.getTitre());
        validateCategorie(a.getCategorie());
        validateDate(dateField.getValue());
        validateContenu(a.getContenu());
        validateImage();
        
        // Mettre à jour l'état du bouton
        updateButtonState();
    }

    private void setupValidationListeners() {
        // Validation du titre
        titreField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTitre(newValue);
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
    }
    
    private void validateTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            titreErrorLabel.setText("Le titre est requis");
            titreErrorLabel.setStyle("-fx-text-fill: red;");
            titreField.setStyle("-fx-border-color: red;");
            validationStatus.put("titre", false);
        } else if (titre.length() < 3) {
            titreErrorLabel.setText("Le titre doit contenir au moins 3 caractères");
            titreErrorLabel.setStyle("-fx-text-fill: red;");
            titreField.setStyle("-fx-border-color: red;");
            validationStatus.put("titre", false);
        } else if (!titre.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,100}$")) {
            titreErrorLabel.setText("Le titre doit contenir uniquement des lettres");
            titreErrorLabel.setStyle("-fx-text-fill: red;");
            titreField.setStyle("-fx-border-color: red;");
            validationStatus.put("titre", false);
        } else {
            titreErrorLabel.setText("✓");
            titreErrorLabel.setStyle("-fx-text-fill: green;");
            titreField.setStyle("-fx-border-color: green;");
            validationStatus.put("titre", true);
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
            dateErrorLabel.setText("Seule la date d'aujourd'hui est autorisée");
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
        } else {
            contenuErrorLabel.setText("✓");
            contenuErrorLabel.setStyle("-fx-text-fill: green;");
            contenuField.setStyle("-fx-border-color: green;");
            validationStatus.put("contenu", true);
        }
        updateButtonState();
    }
    
    private void validateImage() {
        if (imageArticleField.getText() == null || imageArticleField.getText().trim().isEmpty()) {
            imageErrorLabel.setText("Une image est requise");
            imageErrorLabel.setStyle("-fx-text-fill: red;");
            imageArticleField.setStyle("-fx-border-color: red;");
            validationStatus.put("image", false);
        } else {
            imageErrorLabel.setText("✓");
            imageErrorLabel.setStyle("-fx-text-fill: green;");
            imageArticleField.setStyle("-fx-border-color: green;");
            validationStatus.put("image", true);
        }
        updateButtonState();
    }
    
    private void updateButtonState() {
        // Activer le bouton uniquement si tous les champs sont valides
        boolean allValid = validationStatus.values().stream().allMatch(valid -> valid);
        btnModifier.setDisable(!allValid);
        
        if (allValid) {
            btnModifier.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10.0 25.0; -fx-background-radius: 5.0; -fx-font-weight: bold; -fx-font-size: 16.0; -fx-cursor: hand;");
        } else {
            btnModifier.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-padding: 10.0 25.0; -fx-background-radius: 5.0; -fx-font-weight: bold; -fx-font-size: 16.0;");
        }
    }

    @FXML
    void modifierArticle() {
        try {
            articleAModifier.setTitre(titreField.getText().trim());
            articleAModifier.setCategorie(categorieField.getValue());
            articleAModifier.setDate_publication(Date.valueOf(dateField.getValue()));
            articleAModifier.setContenu(contenuField.getText().trim());

            String imageName = (selectedImageFile != null)
                    ? selectedImageFile.getName()
                    : imageArticleField.getText();
            articleAModifier.setImage(imageName);

            articleService.edit(articleAModifier);

            showAlert(Alert.AlertType.INFORMATION, "✅ Article modifié avec succès.");
            ((Stage) btnModifier.getScene().getWindow()).close();

        } catch (ServiceException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Champs invalides ou incomplets.");
        }
    }

    @FXML
    void handleImageUpload() {
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
                
                // Valider l'image après le chargement
                validateImage();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'image.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Modification de l'article");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}