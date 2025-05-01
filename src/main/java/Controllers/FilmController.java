package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import tn.esprit.entities.Film;
import tn.esprit.services.FilmService;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

public class FilmController {

    @FXML
    private TextField titreField;
    @FXML
    private TextField directeurField;
    @FXML
    private TextField dureeField;
    @FXML
    private TextField imageFilmField;
    @FXML
    private Spinner<Double> noteField;
    @FXML
    private ComboBox<String> genreField;
    @FXML
    private DatePicker dateDebutField;
    @FXML
    private DatePicker dateFinField;
    @FXML
    private ImageView filmImageView;
    @FXML
    private HTMLEditor descriptionEditor;

    private File selectedImageFile;

    @FXML
    public void initialize() {
        // Initialisation du Spinner pour la note
        AnchorPane root = (AnchorPane) titreField.getParent().getParent().getParent();
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Adjust layout based on new width
            if (newVal.doubleValue() < 1000) {
                // Compact layout for smaller windows
            } else {
                // Expanded layout for larger windows
            }
        });
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 5.0, 0.5);
        noteField.setValueFactory(valueFactory);

        // Initialisation des genres disponibles
        genreField.getItems().addAll(
                "Action", "Aventure", "Comédie", "Drame",
                "Science-fiction", "Horreur", "Documentaire", "Animation"
        );

        // Configuration de l'éditeur HTML
        descriptionEditor.setHtmlText("<html><body style='font-family:Segoe UI; font-size:14px; color:#333;'>Décris ton film ici...</body></html>");
    }

    @FXML
    private void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            imageFilmField.setText(selectedImageFile.getAbsolutePath());
            filmImageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void addFilm(ActionEvent event) {
        try {
            if (!validateFields()) {
                return;
            }

            Film film = createFilmFromInputs();
            FilmService filmService = new FilmService();
            filmService.ajouter(film);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Film ajouté avec succès !");
            clearForm();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur base de données", "Erreur lors de l'accès à la base de données: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        String titre = titreField.getText().trim();
        String directeur = directeurField.getText().trim();
        String dureeText = dureeField.getText().trim();
        String imagePath = imageFilmField.getText().trim();
        String description = descriptionEditor.getHtmlText().trim();
        Double note = noteField.getValue();
        String genre = genreField.getValue();
        LocalDate dateDebut = dateDebutField.getValue();
        LocalDate dateFin = dateFinField.getValue();

        if (titre.isEmpty() || directeur.isEmpty() || genre == null ||
                dureeText.isEmpty() || dateDebut == null || dateFin == null ||
                imagePath.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Tous les champs doivent être remplis.");
            return false;
        }

        if (titre.length() < 3 || titre.length() > 100) {
            showAlert(Alert.AlertType.WARNING, "Titre invalide", "Le titre doit contenir entre 3 et 100 caractères.");
            return false;
        }

        if (!directeur.matches("[a-zA-Z\\sàâäéèêëîïôöùûüçÀÂÄÉÈÊËÎÏÔÖÙÛÜÇ-]+")) {
            showAlert(Alert.AlertType.WARNING, "Directeur invalide", "Le nom du directeur ne doit contenir que des lettres, espaces et traits d'union.");
            return false;
        }

        try {
            int duree = Integer.parseInt(dureeText);
            if (duree <= 0 || duree > 500) {
                showAlert(Alert.AlertType.WARNING, "Durée invalide", "La durée doit être entre 1 et 500 minutes.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Durée invalide", "La durée doit être un nombre entier valide.");
            return false;
        }

        if (!imagePath.toLowerCase().matches(".*\\.(png|jpg|jpeg)$")) {
            showAlert(Alert.AlertType.WARNING, "Image invalide", "Veuillez sélectionner un fichier image valide (.png, .jpg, .jpeg).");
            return false;
        }

        if (dateDebut.isAfter(dateFin)) {
            showAlert(Alert.AlertType.WARNING, "Dates invalides", "La date de début doit être antérieure à la date de fin.");
            return false;
        }

        if (note == null || note < 0 || note > 10) {
            showAlert(Alert.AlertType.WARNING, "Note invalide", "La note doit être comprise entre 0 et 10.");
            return false;
        }

        return true;
    }

    private Film createFilmFromInputs() {
        Film film = new Film();
        film.setTitre(titreField.getText().trim());
        film.setDirecteur(directeurField.getText().trim());
        film.setNote(noteField.getValue().floatValue());
        film.setGenre(genreField.getValue());
        film.setDuree(Integer.parseInt(dureeField.getText().trim()));
        film.setDateDebut(java.sql.Date.valueOf(dateDebutField.getValue()));
        film.setDateFin(java.sql.Date.valueOf(dateFinField.getValue()));
        film.setImage(imageFilmField.getText().trim());
        film.setDescription(descriptionEditor.getHtmlText().trim());
        film.setNbusers(0);
        return film;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearForm() {
        titreField.clear();
        directeurField.clear();
        noteField.getValueFactory().setValue(5.0);
        genreField.getSelectionModel().clearSelection();
        dureeField.clear();
        dateDebutField.setValue(null);
        dateFinField.setValue(null);
        imageFilmField.clear();
        filmImageView.setImage(null);
        descriptionEditor.setHtmlText("<html><body style='font-family:Segoe UI; font-size:14px; color:#333;'>Décris ton film ici...</body></html>");
    }
}
