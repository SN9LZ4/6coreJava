package Controllers;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import tn.esprit.entities.Film;
import tn.esprit.entities.FilmRating;
import tn.esprit.entities.Salle;
import tn.esprit.services.SalleService;
import tn.esprit.services.TMDBService;
import tn.esprit.services.YouTubeService;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class SalleFrontController implements Initializable {
    @FXML private Label filmTitleLabel;
    @FXML private Label resultCountLabel;
    @FXML private TilePane salleContainer;
    @FXML private ComboBox<String> typeSalleComboBox;
    @FXML private ComboBox<String> etatSalleComboBox;
    @FXML private TextField capaciteMinField;
    @FXML private TextField capaciteMaxField;
    @FXML private Button rechercherButton;
    @FXML private Button reinitialiserButton;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox filmCardContainer;
    @FXML private WebView youtubeWebView;
    @FXML private VBox videoContainer;

    private final SalleService salleService = new SalleService();
    private Film filmAssocie;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Initialisation des ComboBox
        typeSalleComboBox.getItems().addAll("Tous", "Economic", "Standard", "Premium");
        etatSalleComboBox.getItems().addAll("Tous", "Disponible", "En maintenance", "Occupée");

        typeSalleComboBox.setValue("Tous");
        etatSalleComboBox.setValue("Tous");

        // Configuration du ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Style des boutons
        rechercherButton.setStyle("-fx-background-color: #ff0101; -fx-text-fill: white; -fx-font-weight: bold;");
        reinitialiserButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff0101; -fx-border-color: #ff0101; -fx-font-weight: bold;");

        // Initialisation de la WebView YouTube
        youtubeWebView.setContextMenuEnabled(false);

        // Chargement initial des salles
        loadSalles();
    }

    @FXML
    private void onRechercherClick() {
        String type = typeSalleComboBox.getValue();
        String etat = etatSalleComboBox.getValue();
        int capaciteMin = 0;
        int capaciteMax = Integer.MAX_VALUE;

        try {
            if (!capaciteMinField.getText().isEmpty()) {
                capaciteMin = Integer.parseInt(capaciteMinField.getText());
            }
            if (!capaciteMaxField.getText().isEmpty()) {
                capaciteMax = Integer.parseInt(capaciteMaxField.getText());
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Capacité minimale et maximale doivent être des nombres valides.");
            return;
        }

        if (capaciteMin > capaciteMax) {
            showAlert("Erreur de saisie", "La capacité minimale ne peut pas être supérieure à la capacité maximale.");
            return;
        }

        try {
            List<Salle> salles = salleService.recuperer();
            salleContainer.getChildren().clear();

            int count = 0;
            for (Salle salle : salles) {
                boolean match = true;

                if (!"Tous".equals(type) && !salle.getType_salle().equalsIgnoreCase(type)) {
                    match = false;
                }
                if (!"Tous".equals(etat) && !salle.getEtat_salle().equalsIgnoreCase(etat)) {
                    match = false;
                }
                if (salle.getNbr_places() < capaciteMin || salle.getNbr_places() > capaciteMax) {
                    match = false;
                }

                if (match) {
                    salleContainer.getChildren().add(createSalleCard(salle));
                    count++;
                }
            }

            resultCountLabel.setText(count + " salle(s) trouvée(s)");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la recherche des salles.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onResetClick() {
        typeSalleComboBox.setValue("Tous");
        etatSalleComboBox.setValue("Tous");
        capaciteMinField.clear();
        capaciteMaxField.clear();
        loadSalles();
    }

    private void loadSalles() {
        salleContainer.getChildren().clear();
        try {
            List<Salle> salles = filmAssocie != null
                    ? salleService.getSallesParFilm(filmAssocie.getId())
                    : salleService.recuperer();

            for (Salle salle : salles) {
                salleContainer.getChildren().add(createSalleCard(salle));
            }
            resultCountLabel.setText(salles.size() + " salle(s) trouvée(s)");
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les salles.");
            e.printStackTrace();
        }
    }

    private HBox createSalleCard(Salle salle) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 15;");
        card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1)));

        // Image de la salle
        ImageView imageView = new ImageView(getSalleImage(salle));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-radius: 4; -fx-border-color: #e0e0e0;");

        // Détails de la salle
        VBox details = new VBox(10);
        details.getChildren().addAll(
                createStyledLabel(salle.getNom_salle(), true),
                createDetailRow("Type:", salle.getType_salle()),
                createDetailRow("État:", salle.getEtat_salle()),
                createDetailRow("Capacité:", salle.getNbr_places() + " places"),
                createReservationButton()
        );

        card.getChildren().addAll(imageView, details);
        return card;
    }

    private Image getSalleImage(Salle salle) {
        try {
            String imagePath = salle.getImage_salle();
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = imagePath.startsWith("http") || imagePath.startsWith("file:/")
                        ? new Image(imagePath, true)
                        : new Image("file:" + imagePath, true);

                if (!image.isError()) {
                    return image;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }
        return getDefaultImage();
    }

    private Image getDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/default-salle.png"));
        } catch (Exception e) {
            return new Image("https://via.placeholder.com/150");
        }
    }

    private Label createStyledLabel(String text, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18;" + (bold ? "-fx-font-weight: bold;" : ""));
        return label;
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(5);
        Label labelPart = new Label(label);
        labelPart.setStyle("-fx-font-weight: bold;");
        Label valuePart = new Label(value);
        row.getChildren().addAll(labelPart, valuePart);
        return row;
    }

    private Button createReservationButton() {
        Button btn = new Button("Réserver");
        btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setOnAction(event -> handleReservation());
        return btn;
    }

    private void handleReservation() {
        // Implémentez la logique de réservation ici
        showAlert("Réservation", "Fonctionnalité de réservation à implémenter.");
    }



    private VBox createFilmCard(Film film) {
        VBox card = new VBox(20);
        card.setStyle("-fx-padding: 20;");

        HBox content = new HBox(20);

        // Image du film
        ImageView imageView = new ImageView(getFilmImage(film));
        imageView.setFitWidth(240);
        imageView.setFitHeight(360);
        imageView.setPreserveRatio(true);
        imageView.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.3)));
        imageView.setStyle("-fx-border-radius: 4; -fx-border-color: #e0e0e0;");

        // Détails du film
        VBox details = new VBox(15);
        details.setPrefWidth(850);

        // Titre
        Label title = new Label(film.getTitre());
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // Détails principaux
        HBox mainDetails = new HBox(40);
        VBox col1 = new VBox(8);
        col1.getChildren().addAll(
                createDetailRow("Réalisateur:", film.getDirecteur()),
                createDetailRow("Genre:", film.getGenre())
        );

        VBox col2 = new VBox(8);
        col2.getChildren().addAll(
                createDetailRow("Durée:", formatDuration(film.getDuree())),
                createRatingBox(film.getNote())
        );
        mainDetails.getChildren().addAll(col1, col2);

        // Dates
        HBox dates = new HBox(20);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        dates.getChildren().addAll(
                createDetailRow("Du:", dateFormat.format(film.getDateDebut())),
                createDetailRow("au:", dateFormat.format(film.getDateFin()))
        );

        // Synopsis
        VBox synopsis = new VBox(5);
        Label synopsisTitle = new Label("Synopsis");
        synopsisTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

// Remplacer le TextArea par un WebView
        WebView descriptionWebView = new WebView();
        WebEngine webEngine = descriptionWebView.getEngine();

// Créer le contenu HTML stylisé
        String descriptionHtml = "<html><body style='"
                + "font-family: Arial, sans-serif; "
                + "padding: 10px; "
                + "color: #333333; "
                + "background-color: #f9f9f9; "
                + "border-radius: 5px; "
                + "border: 1px solid #e0e0e0;"
                + "'>"
                + (film.getDescription() != null ? film.getDescription() : "Aucune description disponible")
                + "</body></html>";

        webEngine.loadContent(descriptionHtml);

// Configuration du WebView
        descriptionWebView.setPrefHeight(150); // Ajustez selon vos besoins
        descriptionWebView.setPrefWidth(300);  // Ajustez selon vos besoins

        synopsis.getChildren().addAll(synopsisTitle, descriptionWebView);

// Assemblage des détails
        details.getChildren().addAll(title, mainDetails, dates, synopsis);
        content.getChildren().addAll(imageView, details);
        card.getChildren().add(content);

        return card;
    }

    private Image getFilmImage(Film film) {
        try {
            String imagePath = film.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = imagePath.startsWith("http") || imagePath.startsWith("file:/")
                        ? new Image(imagePath, true)
                        : new Image("file:" + imagePath, true);

                if (!image.isError()) {
                    return image;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }
        return getDefaultImage();
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return (hours > 0 ? hours + "h " : "") + mins + "min";
    }

    private HBox createRatingBox(double rating) {
        HBox box = new HBox(5);
        Label label = new Label("Note:");
        Label value = new Label(String.format("%.1f/10", rating));
        value.setStyle("-fx-text-fill: #4169e1; -fx-font-weight: bold;");

        HBox stars = new HBox(2);
        double starRating = rating / 2; // Convertir en notation sur 5
        for (int i = 1; i <= 5; i++) {
            Label star = new Label(i <= starRating ? "★" : "☆");
            star.setStyle("-fx-text-fill: " + (i <= starRating ? "#4169e1" : "#cccccc") + ";");
            stars.getChildren().add(star);
        }

        box.getChildren().addAll(label, value, stars);
        return box;
    }

    public void loadTrailerForFilm(String filmTitle) {
        videoContainer.setVisible(true);
        String videoId = YouTubeService.getTrailerVideoId(filmTitle);

        if (videoId != null) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=0";
            youtubeWebView.getEngine().load(embedUrl);
        } else {
            youtubeWebView.getEngine().loadContent(
                    "<html><body style='display:flex; justify-content:center; align-items:center; height:100%;'>" +
                            "<h3 style='color:#666;'>Aucune bande-annonce disponible</h3>" +
                            "</body></html>"
            );
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Ajoutez ces nouvelles déclarations
    @FXML private VBox recommendationsContainer;
    private final TMDBService tmdbService = new TMDBService();

    // Modifiez la méthode setFilm
    public void setFilm(Film film) {
        this.filmAssocie = film;
        filmTitleLabel.setText("Film sélectionné : " + film.getTitre());

        // Charger les infos du film
        filmCardContainer.getChildren().clear();
        filmCardContainer.getChildren().add(createFilmCard(film));
        setupRatingSystem();
        // Charger la bande-annonce
        loadTrailerForFilm(film.getTitre());

        // Charger les salles
        loadSalles();

        // Charger les recommandations
        loadRecommendations(film.getTitre());
    }

    private void loadRecommendations(String movieTitle) {
        recommendationsContainer.getChildren().clear();

        Label title = new Label("Recommandations Similaires");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");
        recommendationsContainer.getChildren().add(title);

        List<Film> similarMovies = tmdbService.getSimilarMovies(movieTitle);

        if (similarMovies.isEmpty()) {
            Label noResults = new Label("Aucune recommandation trouvée");
            recommendationsContainer.getChildren().add(noResults);
            return;
        }

        HBox moviesContainer = new HBox(15);
        moviesContainer.setStyle("-fx-padding: 10 0;");

        for (Film movie : similarMovies) {
            moviesContainer.getChildren().add(createRecommendationCard(movie));
        }

        recommendationsContainer.getChildren().add(moviesContainer);
    }

    private VBox createRecommendationCard(Film film) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 10;");
        card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

        ImageView imageView = new ImageView(new Image(film.getImage(), 150, 225, true, true));
        imageView.setStyle("-fx-border-radius: 4;");

        Label title = new Label(film.getTitre());
        title.setStyle("-fx-font-weight: bold; -fx-wrap-text: true;");
        title.setMaxWidth(150);

        HBox ratingBox = new HBox(5);
        ratingBox.getChildren().addAll(
                new Label(String.format("%.1f", film.getNote())),
                new Label("★")
        );
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8; -fx-padding: 10;");
            card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 10;");
            card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
        });



        card.getChildren().addAll(imageView, title, ratingBox);
        return card;
    }
    // Déclarations supplémentaires
    @FXML private HBox interactiveStars;
    @FXML private Label star1, star2, star3, star4, star5;
    @FXML private Label averageRatingLabel;
    @FXML private HBox averageStars;
    @FXML private Label ratingCountLabel;

    private int currentUserRating = 0;

    // Dans initialize()
    private void setupRatingSystem() {
        setupInteractiveStars();
        updateAverageRatingDisplay();
    }

    private void setupInteractiveStars() {
        Label[] stars = {star1, star2, star3, star4, star5};

        for (int i = 0; i < stars.length; i++) {
            final int rating = i + 1;
            Label star = stars[i];

            star.setOnMouseEntered(e -> highlightStars(rating));
            star.setOnMouseExited(e -> resetStars());
            star.setOnMouseClicked(e -> {
                currentUserRating = rating;
                FilmRating.addRating(rating);
                updateAverageRatingDisplay();
            });
        }
    }

    private void highlightStars(int upTo) {
        Label[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            stars[i].setText(i < upTo ? "★" : "☆");
            stars[i].setStyle("-fx-text-fill: " + (i < upTo ? "#FFD700" : "#ccc") + "; -fx-font-size: 24px;");
        }
    }

    private void resetStars() {
        highlightStars(currentUserRating);
    }

    private void updateAverageRatingDisplay() {
        double average = FilmRating.getAverage();
        averageRatingLabel.setText(String.format("%.1f/5", average));
        ratingCountLabel.setText("(" + FilmRating.getRatingCount() + " votes)");

        updateAverageStars(average);
    }

    private void updateAverageStars(double average) {
        averageStars.getChildren().clear();

        int fullStars = (int) average;
        boolean hasHalfStar = (average - fullStars) >= 0.5;

        // Ajouter les étoiles pleines
        for (int i = 0; i < fullStars; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16px;");
            averageStars.getChildren().add(star);
        }

        // Ajouter une demi-étoile si nécessaire
        if (hasHalfStar) {
            Label star = new Label("½");
            star.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16px;");
            averageStars.getChildren().add(star);
        }

        // Ajouter les étoiles vides
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for (int i = 0; i < remainingStars; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16px;");
            averageStars.getChildren().add(star);
        }
    }

    // Animation des étoiles
    private void animateStar(Label star) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), star);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }


}