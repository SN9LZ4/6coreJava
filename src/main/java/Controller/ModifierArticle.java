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

public class ModifierArticle {

    @FXML
    private TextField titreField;

    @FXML
    private ComboBox<String> categorieField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField contenuField;

    @FXML
    private TextField imageArticleField;

    @FXML
    private ImageView articleImageView;

    @FXML
    private Button btnModifier;

    private File selectedImageFile;

    private final ArticleService articleService = new ArticleService();

    private article articleAModifier;

    // Injecte l'article à modifier
    public void setArticle(article a) {
        this.articleAModifier = a;

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

        // Empêche de choisir une date autre qu’aujourd’hui
        dateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });
    }

    @FXML
    void modifierArticle() {
        if (!validateInputs()) return;

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

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l’image.");
            }
        }
    }

    private boolean validateInputs() {
        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();
        String categorie = categorieField.getValue();
        LocalDate selectedDate = dateField.getValue();

        StringBuilder errors = new StringBuilder();

        if (titre.isEmpty() || titre.length() < 3 || !titre.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,100}$")) {
            errors.append("🔸 Le titre doit contenir uniquement des lettres et avoir entre 3 et 100 caractères.\n");
        }

        if (categorie == null || categorie.isEmpty()) {
            errors.append("🔸 La catégorie est obligatoire.\n");
        }

        if (selectedDate == null || !selectedDate.equals(LocalDate.now())) {
            errors.append("🔸 La date doit être celle d’aujourd’hui uniquement.\n");
        }

        if (contenu.isEmpty() || contenu.length() < 10 || !contenu.matches(".*[a-zA-ZÀ-ÿ].*")) {
            errors.append("🔸 Le contenu doit contenir au moins 10 caractères et inclure des lettres.\n");
        }

        String imageName = (selectedImageFile != null)
                ? selectedImageFile.getName()
                : imageArticleField.getText().trim();

        if (imageName.isEmpty() || !imageName.matches(".*\\.(png|jpg|jpeg)$")) {
            errors.append("🔸 Veuillez sélectionner une image au format .png, .jpg ou .jpeg.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, errors.toString());
            return false;
        }

        return true;
    }


    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Modification de l'article");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
