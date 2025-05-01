package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Film;
import tn.esprit.services.FilmService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;

public class ModifierFilmController {

    private static final String IMAGE_DIR = "src/main/resources/images";

    @FXML private TextField titreField;
    @FXML private TextField directeurField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private Spinner<Double> noteSpinner;
    @FXML private Spinner<Integer> dureeSpinner;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ImageView imagePreview;
    @FXML private HTMLEditor descriptionEditor;
    @FXML private Button saveButton;
    @FXML private Button uploadImageButton;

    private Film film;
    private final FilmService filmService = new FilmService();

    @FXML
    public void initialize() {
        // Remplir la ComboBox genre
        genreComboBox.getItems().addAll(
                "Action", "Comédie", "Drame", "Fantastique", "Horreur",
                "Romance", "Science-fiction", "Thriller", "Documentaire", "Animation"
        );

        // Spinner pour note (de 0 à 10 avec pas de 0.1)
        SpinnerValueFactory<Double> noteFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 5.0, 0.1);
        noteSpinner.setValueFactory(noteFactory);
        noteSpinner.setEditable(false);

        // Spinner pour durée (de 30 à 300 minutes)
        SpinnerValueFactory<Integer> dureeFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 300, 90);
        dureeSpinner.setValueFactory(dureeFactory);
        dureeSpinner.setEditable(false);
    }

    public void setFilm(Film film) {
        this.film = film;

        titreField.setText(film.getTitre());
        directeurField.setText(film.getDirecteur());
        genreComboBox.setValue(film.getGenre());
        noteSpinner.getValueFactory().setValue((double) film.getNote());
        dureeSpinner.getValueFactory().setValue(film.getDuree());
        dateDebutPicker.setValue(film.getDateDebut().toLocalDate());
        dateFinPicker.setValue(film.getDateFin().toLocalDate());
        descriptionEditor.setHtmlText(film.getDescription());

        loadImage(film.getImage());
    }

    private void loadImage(String imageName) {
        File imageFile = (imageName != null && !imageName.isEmpty()) ? new File(IMAGE_DIR, imageName) : null;

        if (imageFile != null && imageFile.exists()) {
            imagePreview.setImage(new Image(imageFile.toURI().toString()));
        } else {
            File defaultImg = new File(IMAGE_DIR, "default.jpg");
            imagePreview.setImage(new Image(defaultImg.toURI().toString()));
        }
    }

    @FXML
    void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String imageName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destDir = new File(IMAGE_DIR);
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, imageName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                film.setImage(imageName);
                imagePreview.setImage(new Image(destFile.toURI().toString()));

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "❌ Erreur lors du chargement de l’image.");
            }
        }
    }

    @FXML
    void handleSave() {
        try {
            if (!validateFields()) return;

            film.setTitre(titreField.getText().trim());
            film.setDirecteur(directeurField.getText().trim());
            film.setGenre(genreComboBox.getValue());
            film.setNote(noteSpinner.getValue().floatValue());
            film.setDuree(dureeSpinner.getValue());
            film.setDateDebut(Date.valueOf(dateDebutPicker.getValue()));
            film.setDateFin(Date.valueOf(dateFinPicker.getValue()));
            film.setDescription(descriptionEditor.getHtmlText());

            filmService.edit(film);

            showAlert(Alert.AlertType.INFORMATION, "✅ Film modifié avec succès.");
            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Erreur lors de la modification du film :\n" + e.getMessage());
        }
    }

    private boolean validateFields() {
        String titre = titreField.getText().trim();
        String directeur = directeurField.getText().trim();

        if (titre.isEmpty() || directeur.isEmpty() || genreComboBox.getValue() == null
                || noteSpinner.getValue() == null || dureeSpinner.getValue() == null
                || dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        if (!titre.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,100}$")) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Le titre doit contenir uniquement des lettres et avoir entre 3 et 100 caractères.");
            return false;
        }

        if (!directeur.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,50}$")) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Le nom du directeur doit contenir uniquement des lettres et avoir entre 3 et 50 caractères.");
            return false;
        }

        if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
            showAlert(Alert.AlertType.WARNING, "⚠️ La date de fin ne peut pas être avant la date de début.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Modification du film");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
