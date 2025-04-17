package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import tn.esprit.entities.Film;
import tn.esprit.entities.Salle;
import tn.esprit.services.SalleService;

import java.io.File;
import java.net.URL;
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

    private SalleService salleService = new SalleService();
    private Film filmAssocie;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeSalleComboBox.getItems().addAll("Tous", "Economic", "Standard", "Premium");
        etatSalleComboBox.getItems().addAll("Tous", "Disponible", "En maintenance", "Occupée");

        typeSalleComboBox.setValue("Tous");
        etatSalleComboBox.setValue("Tous");

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        rechercherButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        reinitialiserButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        loadSalles(); // Initial load
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
            new Alert(Alert.AlertType.ERROR, "Capacité minimale et maximale doivent être des nombres.").show();
            return;
        }

        try {
            List<Salle> salles = salleService.recuperer();
            salleContainer.getChildren().clear();

            int count = 0;
            for (Salle salle : salles) {
                boolean match = true;

                if (!type.equals("Tous") && !salle.getType_salle().equalsIgnoreCase(type)) match = false;
                if (!etat.equals("Tous") && !salle.getEtat_salle().equalsIgnoreCase(etat)) match = false;
                if (salle.getNbr_places() < capaciteMin || salle.getNbr_places() > capaciteMax) match = false;

                if (match) {
                    salleContainer.getChildren().add(createSalleCard(salle));
                    count++;
                }
            }

            resultCountLabel.setText(count + " salle(s) trouvée(s)");
        } catch (Exception e) {
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
            List<Salle> salles = salleService.recuperer();
            for (Salle salle : salles) {
                salleContainer.getChildren().add(createSalleCard(salle));
            }
            resultCountLabel.setText(salles.size() + " salle(s) trouvée(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createSalleCard(Salle salle) {
        HBox card = new HBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 15; -fx-spacing: 15;");
        card.setEffect(new DropShadow(10, Color.GRAY));

        Image image;
        try {
            String imagePath = salle.getImage_salle();
            if (imagePath != null && !imagePath.isEmpty()) {
                image = imagePath.startsWith("http") || imagePath.startsWith("file:/")
                        ? new Image(imagePath, true)
                        : new Image("file:" + imagePath, true);

                if (image.isError()) {
                    image = getDefaultImage();
                    System.err.println("Erreur image salle: " + imagePath);
                }
            } else {
                image = getDefaultImage();
            }
        } catch (Exception e) {
            System.err.println("Exception chargement image: " + e.getMessage());
            image = getDefaultImage();
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        VBox details = new VBox(10);
        details.getChildren().addAll(
                labelStyled(salle.getNom_salle(), true),
                new Label("Type: " + salle.getType_salle()),
                new Label("État: " + salle.getEtat_salle()),
                new Label("Capacité: " + salle.getNbr_places() + " places"),
                buttonReserver()
        );

        card.getChildren().addAll(imageView, details);
        return card;
    }

    private Label labelStyled(String text, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18;" + (bold ? " -fx-font-weight: bold;" : ""));
        return label;
    }

    private Button buttonReserver() {
        Button btn = new Button("Réserver");
        btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        return btn;
    }

    private Image getDefaultImage() {
        try {
            URL defaultImageUrl = getClass().getResource("/images/default-salle.png");
            if (defaultImageUrl != null) {
                return new Image(defaultImageUrl.toExternalForm());
            } else {
                System.err.println("Image par défaut introuvable !");
                return new Image("https://via.placeholder.com/150");
            }
        } catch (Exception e) {
            return new Image("https://via.placeholder.com/150");
        }
    }

    public void setFilm(Film film) {
        this.filmAssocie = film;
        filmTitleLabel.setText(film.getTitre());
        afficherSallesPourCeFilm();
    }

    private void afficherSallesPourCeFilm() {
        if (filmAssocie == null) return;

        salleContainer.getChildren().clear();
        try {
            List<Salle> salles = salleService.getSallesParFilm(filmAssocie.getId());
            for (Salle salle : salles) {
                salleContainer.getChildren().add(createSalleCard(salle));
            }
            resultCountLabel.setText(salles.size() + " salle(s) trouvée(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
