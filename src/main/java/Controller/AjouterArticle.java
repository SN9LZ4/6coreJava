package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
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

public class AjouterArticle {

    @FXML
    private TextField nomField;

    @FXML
    private ComboBox<String> categorieField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField imageArticleField;

    @FXML
    private ImageView articleImageView;

    @FXML
    private Button btnAjouter;

    @FXML
    private TextField contenuField;

    private File selectedImageFile;

    private final ArticleService articleService = new ArticleService();

    @FXML
    public void initialize() {
        categorieField.getItems().addAll("film", "auteur", "cinema", "snacks", "autre");

        dateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });

        dateField.setValue(LocalDate.now());
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

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Erreur lors du chargement de l’image.");
            }
        }
    }

    @FXML
    void addArticle(ActionEvent event) {
        if (!validateInputs()) return;

        try {
            String titre = nomField.getText().trim();
            String categorie = categorieField.getValue();
            LocalDate selectedDate = dateField.getValue();
            Date date = Date.valueOf(selectedDate);
            String contenu = contenuField.getText().trim();
            String imagePath = selectedImageFile.getName();

            article a = new article(
                    titre, date, contenu, imagePath,
                    0.0,
                    categorie,
                    0, 0, 0
            );

            articleService.add(a);
            showAlert(AlertType.INFORMATION, "✅ Article ajouté avec succès !");
            ((Stage) btnAjouter.getScene().getWindow()).close();
            openAfficherArticlePage();

        } catch (ServiceException e) {
            showAlert(AlertType.ERROR, "Erreur : " + e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur inattendue : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        String titre = nomField.getText().trim();
        String contenu = contenuField.getText().trim();
        String categorie = categorieField.getValue();
        LocalDate selectedDate = dateField.getValue();

        StringBuilder errors = new StringBuilder();

        if (titre.isEmpty() || titre.length() < 3 || !titre.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,100}$")) {
            errors.append("🔸 Le nom doit contenir uniquement des lettres et avoir entre 3 et 100 caractères.\n");
        }

        if (categorie == null || categorie.isEmpty()) {
            errors.append("🔸 La catégorie est obligatoire.\n");
        }

        if (selectedDate == null || !selectedDate.equals(LocalDate.now())) {
            errors.append("🔸 La date doit être celle d’aujourd’hui uniquement.\n");
        }

        if (contenu.isEmpty() || contenu.length() < 10 || !contenu.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,100}$")) {
            errors.append("🔸 Le contenu doit contenir au moins 10 caractères et ne pas contenir uniquement des chiffres.\n");
        }

        if (selectedImageFile == null || !selectedImageFile.getName().matches(".*\\.(png|jpg|jpeg)$")) {
            errors.append("🔸 Veuillez sélectionner une image au format .png, .jpg ou .jpeg.\n");
        }

        if (errors.length() > 0) {
            showAlert(AlertType.ERROR, errors.toString());
            return false;
        }

        return true;
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Ajouter Article");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        nomField.clear();
        categorieField.setValue(null);
        dateField.setValue(LocalDate.now());
        contenuField.clear();
        imageArticleField.clear();
        articleImageView.setImage(null);
        selectedImageFile = null;
    }

    private void openAfficherArticlePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Articles");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Impossible d’ouvrir la page AfficherArticle.");
        }
    }
}
