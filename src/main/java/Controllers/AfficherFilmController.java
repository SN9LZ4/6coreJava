package Controllers;

import com.google.protobuf.ServiceException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.entities.Film;
import tn.esprit.services.FilmService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class AfficherFilmController {

    @FXML
    private ListView<Film> rlist;
    @FXML
    private Label titreLabel, directeurLabel, noteLabel, genreLabel, dureeLabel, dateDebutLabel, dateFinLabel;
    @FXML
    private ImageView filmImageView;
    @FXML
    private WebView descriptionWebView;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    // Champs manquants pour la méthode handleSave
    @FXML
    private TextField titreField, directeurField, genreField, imageField, noteField, dureeField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker dateDebutField, dateFinField;
    @FXML
    private Button btnSave;

    private final FilmService filmService = new FilmService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Film film;

    @FXML
    public void initialize() {
        try {
            loadFilms();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des films: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFilms() throws SQLException {
        List<Film> films = filmService.recuperer();

        if (films == null || films.isEmpty()) {
            showAlert("Information", "Aucun film trouvé.");
            return;
        }

        Platform.runLater(() -> {
            rlist.setItems(FXCollections.observableArrayList(films));

            rlist.setCellFactory(list -> new ListCell<Film>() {
                @Override
                protected void updateItem(Film film, boolean empty) {
                    super.updateItem(film, empty);
                    if (empty || film == null) {
                        setText(null);
                    } else {
                        String noteText = film.getNote() > 0 ? String.format(" (%.1f/10)", film.getNote()) : " (Non noté)";
                        setText(film.getTitre() + noteText);
                    }
                }
            });

            rlist.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldFilm, newFilm) -> afficherFilm(newFilm)
            );

            if (!films.isEmpty()) {
                rlist.getSelectionModel().selectFirst();
            }
        });
    }

    public void afficherFilm(Film film) {
        if (film == null) return;

        Platform.runLater(() -> {
            // Texte simple
            titreLabel.setText(film.getTitre() != null ? film.getTitre() : "Non spécifié");
            directeurLabel.setText(film.getDirecteur() != null ? film.getDirecteur() : "Non spécifié");
            genreLabel.setText(film.getGenre() != null ? film.getGenre() : "Non spécifié");

            // Note avec formatage
            noteLabel.setText(film.getNote() > 0 ? String.format("%.1f/10", film.getNote()) : "Non noté");

            // Durée
            dureeLabel.setText(film.getDuree() > 0 ? film.getDuree() + " min" : "Durée non spécifiée");

            // Dates avec formatage
            try {
                dateDebutLabel.setText(film.getDateDebut() != null ?
                        dateFormat.format(film.getDateDebut()) : "Non spécifiée");
                dateFinLabel.setText(film.getDateFin() != null ?
                        dateFormat.format(film.getDateFin()) : "Non spécifiée");
            } catch (Exception e) {
                dateDebutLabel.setText("Format date invalide");
                dateFinLabel.setText("Format date invalide");
            }

            // Description HTML
            String descriptionHtml = "<html><body style='font-family:sans-serif; padding:10px;'>" +
                    (film.getDescription() != null ? film.getDescription() : "Aucune description disponible") +
                    "</body></html>";
            WebEngine webEngine = descriptionWebView.getEngine();
            webEngine.loadContent(descriptionHtml);

            // Image
            try {
                if (film.getImage() != null && !film.getImage().isEmpty()) {
                    String imagePath = film.getImage();

                    Image img;
                    if (imagePath.startsWith("http") || imagePath.startsWith("file:/")) {
                        // Chargement depuis une URL ou un chemin local déjà bien formaté
                        img = new Image(imagePath, true);
                    } else {
                        // Ajout du préfixe file: pour charger depuis le disque local
                        img = new Image("file:" + imagePath, true);
                    }

                    img.errorProperty().addListener((obs, wasError, isNowError) -> {
                        if (isNowError) {
                            filmImageView.setImage(null);
                            System.err.println("Erreur lors du chargement de l'image: " + imagePath);
                        }
                    });

                    filmImageView.setImage(img);
                } else {
                    filmImageView.setImage(null); // Aucun chemin, aucune image
                }
            } catch (Exception e) {
                filmImageView.setImage(null);
                System.err.println("Erreur chargement image: " + e.getMessage());
            }

        });
    }

    @FXML
    public void handleRetour() {
        System.out.println("Retour à la page précédente");
        // Ajouter ici le code pour revenir à la page précédente
    }

    private void showAlert(String titre, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Version surchargée pour supporter les différents types d'alertes
    private void showAlert(Alert.AlertType type, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Méthode de rafraîchissement de la liste
    public void refreshFilmList() {
        try {
            loadFilms();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement: " + e.getMessage());
        }
    }

    public void setRlist() {
        // Logique pour définir la liste si nécessaire
    }

    @FXML
    private void handleModify(ActionEvent event) {
        Film selectedFilm = rlist.getSelectionModel().getSelectedItem();

        if (selectedFilm == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un film à modifier.");
            return;
        }

        try {
            // Charger la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFilm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur associé et lui passer le film sélectionné
            ModifierFilmController controller = loader.getController();
            controller.setFilm(selectedFilm);

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier Film");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Rafraîchir la liste des films après modification
            refreshFilmList();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la fenêtre de modification.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Film selectedFilm = rlist.getSelectionModel().getSelectedItem();
        if (selectedFilm == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un film à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous vraiment supprimer le film : " + selectedFilm.getTitre() + " ?");
        alert.setContentText("Cette action est irréversible.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                filmService.supprimer(selectedFilm);
                rlist.getItems().remove(selectedFilm);
                afficherFilm(null);
                showAlert("Succès", "Le film a été supprimé avec succès.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    private void handleAjouterFilm(ActionEvent event) throws IOException {
        // Load the AjouterFilm.fxml view
        Parent root = FXMLLoader.load(getClass().getResource("/ajouterFilm.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void handleSave() {
        try {
            // Vérification que les champs obligatoires sont remplis
            if (titreField.getText().isEmpty() || genreField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Vérification que les champs numériques sont valides
            float note;
            int duree;
            try {
                note = Float.parseFloat(noteField.getText());
                if (note < 0 || note > 10) {
                    showAlert(Alert.AlertType.ERROR, "La note doit être comprise entre 0 et 10.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "La note doit être un nombre valide.");
                return;
            }

            try {
                duree = Integer.parseInt(dureeField.getText());
                if (duree <= 0) {
                    showAlert(Alert.AlertType.ERROR, "La durée doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "La durée doit être un nombre entier valide.");
                return;
            }

            // Vérification des dates
            if (dateDebutField.getValue() == null || dateFinField.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Les dates doivent être spécifiées.");
                return;
            }

            if (dateFinField.getValue().isBefore(dateDebutField.getValue())) {
                showAlert(Alert.AlertType.ERROR, "La date de fin ne peut pas être antérieure à la date de début.");
                return;
            }

            // Logique de sauvegarde des données
            film.setTitre(titreField.getText());
            film.setDirecteur(directeurField.getText());
            film.setGenre(genreField.getText());
            film.setDescription(descriptionField.getText());
            film.setNote(note);
            film.setDuree(duree);
            film.setDateDebut(Date.valueOf(dateDebutField.getValue()));
            film.setDateFin(Date.valueOf(dateFinField.getValue()));
            film.setImage(imageField.getText()); // Correction: utiliser setImage au lieu de setImageFilm

            // Appel à un service pour sauvegarder les modifications
            filmService.edit(film);

            showAlert(Alert.AlertType.INFORMATION, "Film modifié avec succès.");
            ((Stage) btnSave.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification : " + e.getMessage());
        }
    }
}