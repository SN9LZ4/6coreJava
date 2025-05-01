package Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.entities.Film;
import tn.esprit.services.FilmService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FrontController {

    @FXML private TextField titreField;
    @FXML private TextField directeurField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private TextField noteField;
    @FXML private TilePane filmContainer;


    private final FilmService filmService = new FilmService();

    @FXML
    public void initialize() {
        genreComboBox.setItems(FXCollections.observableArrayList(
                "Action", "Comédie", "Drame", "Horreur", "Science-fiction", "Animation", "Aventure"
        ));
        try {
            afficherTousLesFilms();
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", "Impossible de charger les films.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onRechercherClick() {
        String titre = titreField.getText().trim();
        String directeur = directeurField.getText().trim();
        String genre = genreComboBox.getValue();
        Double note = null;

        if (!noteField.getText().trim().isEmpty()) {
            try {
                note = Double.parseDouble(noteField.getText().trim());
            } catch (NumberFormatException e) {
                afficherErreur("Note invalide", "Veuillez entrer une note valide (ex: 7.5).");
                return;
            }
        }

        List<Film> films = filmService.rechercher(titre, directeur, genre, note);
        afficherFilms(films);
    }

    @FXML
    private void onResetClick() {
        titreField.clear();
        directeurField.clear();
        genreComboBox.getSelectionModel().clearSelection();
        noteField.clear();
        try {
            afficherTousLesFilms();
        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible d'afficher les films.");
        }
    }

    private void afficherTousLesFilms() throws SQLException {
        List<Film> films = filmService.recuperer();
        afficherFilms(films);
    }

    private void afficherFilms(List<Film> films) {
        filmContainer.getChildren().clear();

        if (films.isEmpty()) {
            Label emptyLabel = new Label("Aucun film trouvé.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            filmContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Film film : films) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(15));
            card.setStyle("""
                -fx-border-color: #ccc;
                -fx-background-color: #ffffff;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);
            """);

            ImageView imageView = new ImageView();
            imageView.setFitWidth(300);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            chargerImage(film.getImage(), imageView);

            Label titre = new Label(film.getTitre());
            titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            WebView descriptionView = new WebView();
            descriptionView.setPrefHeight(100);
            descriptionView.getEngine().loadContent(film.getDescription());

            Label directeur = new Label("🎬 Directeur : " + film.getDirecteur());
            directeur.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            Label note = new Label("⭐ Note : " + film.getNote() + "/10");
            note.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12;");

            Label duree = new Label("⏱️ Durée : " + film.getDuree() + " minutes");
            duree.setStyle("-fx-font-size: 14px; -fx-text-fill: #16a085;");

            Button showBtn = new Button("Voir plus");
            showBtn.setStyle("""
                -fx-background-color: #4169e1;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 10px 20px;
                -fx-background-radius: 5px;
            """);
            showBtn.setOnAction(e -> afficherDetailFilm(film));

            card.getChildren().addAll(imageView, titre, descriptionView, directeur, note, duree, showBtn);
            filmContainer.getChildren().add(card);
        }
    }

    private void chargerImage(String path, ImageView imageView) {
        if (path == null || path.trim().isEmpty()) {
            imageView.setImage(null);
            return;
        }

        try {
            Image img = path.startsWith("http") || path.startsWith("file:/") ?
                    new Image(path, true) : new Image("file:" + path, true);

            img.errorProperty().addListener((obs, wasError, isNowError) -> {
                if (isNowError) {
                    imageView.setImage(null);
                    System.err.println("Erreur chargement image: " + path);
                }
            });

            imageView.setImage(img);
        } catch (Exception e) {
            imageView.setImage(null);
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }

    private void afficherDetailFilm(Film film) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SalleFront.fxml"));
            AnchorPane salleFrontPane = loader.load();

            SalleFrontController controller = loader.getController();
            controller.setFilm(film); // On envoie le film sélectionné à la page SalleFront

            Stage stage = new Stage();
            stage.setTitle("Salles pour : " + film.getTitre());
            stage.setScene(new Scene(salleFrontPane));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la page SalleFront.").show();
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
