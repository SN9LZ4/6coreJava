package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import tn.esprit.entities.Film;
import tn.esprit.services.FilmService;

import java.io.File;
import java.time.LocalDate;

public class FilmController {

    @FXML
    private TextField titreField, directeurField, dureeField, imageFilmField;

    @FXML
    private Spinner<Double> noteField;

    @FXML
    private ComboBox<String> genreField;

    @FXML
    private DatePicker dateDebutField, dateFinField;

    @FXML
    private ImageView filmImageView;

    @FXML
    private WebView descriptionEditor;

    private WebEngine webEngine;

    private File selectedImageFile;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 5.0, 0.1);
        noteField.setValueFactory(valueFactory);

        genreField.getItems().addAll("Action", "Comédie", "Drame", "Science-fiction", "Horreur", "Documentaire");

        webEngine = descriptionEditor.getEngine();
        webEngine.loadContent("<html><body contenteditable='true' style='font-family:Segoe UI; font-size:14px; color:#333;'>Décris ton film ici...</body></html>");
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", ".png", ".jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            imageFilmField.setText(file.getAbsolutePath());
            filmImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void addFilm() {
        try {
            String titre = titreField.getText().trim();
            String directeur = directeurField.getText().trim();
            Double note = noteField.getValue();
            String genre = genreField.getValue();
            String dureeText = dureeField.getText().trim();
            LocalDate dateDebut = dateDebutField.getValue();
            LocalDate dateFin = dateFinField.getValue();
            String imagePath = imageFilmField.getText().trim();
            String description = (String) webEngine.executeScript("document.body.innerHTML");

            if (titre.isEmpty() || directeur.isEmpty() || genre == null || dureeText.isEmpty()
                    || dateDebut == null || dateFin == null || imagePath.isEmpty() || description.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Tous les champs doivent être remplis.");
                return;
            }

            if (titre.length() < 3 || titre.length() > 100) {
                showAlert(Alert.AlertType.WARNING, "Titre invalide", "Le titre doit contenir entre 3 et 100 caractères.");
                return;
            }

            if (directeur.length() < 3 || directeur.length() > 50) {
                showAlert(Alert.AlertType.WARNING, "Directeur invalide", "Le nom du directeur doit contenir entre 3 et 50 caractères.");
                return;
            }

// Vérifie que le nom contient uniquement des lettres et espaces
            if (!directeur.matches("[a-zA-Z\\s]+")) {
                showAlert(Alert.AlertType.WARNING, "Directeur invalide", "Le nom du directeur ne doit contenir que des lettres.");
                return;
            }

            if (!imagePath.endsWith(".png") && !imagePath.endsWith(".jpg") && !imagePath.endsWith(".jpeg")) {
                showAlert(Alert.AlertType.WARNING, "Image invalide", "Veuillez sélectionner un fichier image valide (.png, .jpg, .jpeg).");
                return;
            }

            int duree;
            try {
                duree = Integer.parseInt(dureeText);
                if (duree <= 0 || duree > 500) {
                    showAlert(Alert.AlertType.WARNING, "Durée invalide", "La durée doit être un entier positif inférieur à 500 minutes.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "La durée doit être un entier valide.");
                return;
            }

            if (dateDebut.isAfter(dateFin)) {
                showAlert(Alert.AlertType.WARNING, "Erreur de dates", "La date de début doit précéder la date de fin.");
                return;
            }

            Film film = new Film();
            film.setTitre(titre);
            film.setDirecteur(directeur);
            film.setNote(note.floatValue());
            film.setGenre(genre);
            film.setDuree(duree);
            film.setDateDebut(java.sql.Date.valueOf(dateDebut));
            film.setDateFin(java.sql.Date.valueOf(dateFin));
            film.setImage(imagePath);
            film.setDescription(description);
            film.setNbusers(0);

            FilmService fs = new FilmService();
            fs.ajouter(film);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Film ajouté avec succès !");
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout du film.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
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
        webEngine.loadContent("<html><body contenteditable='true' style='font-family:Segoe UI; font-size:14px; color:#333;'>Décris ton film ici...</body></html>");
    }
}