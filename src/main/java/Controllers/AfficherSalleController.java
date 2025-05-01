package Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.entities.Film;
import tn.esprit.entities.Salle;
import tn.esprit.services.SalleService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AfficherSalleController {

    @FXML
    private ListView<Salle> salleListView;

    @FXML
    private ImageView salleImageView;

    @FXML
    private Label nomLabel;

    @FXML
    private Label placesLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label etatLabel;

    @FXML
    private WebView descriptionWebView;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button retourButton;

    @FXML
    private Button ajouterButton;

    private final SalleService salleService = new SalleService();

    @FXML
    public void initialize() {
        try {
            List<Salle> salles = salleService.recuperer();
            salleListView.setItems(FXCollections.observableArrayList(salles));

            salleListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    displaySalleDetails(newVal);
                }
            });

            if (!salles.isEmpty()) {
                salleListView.getSelectionModel().selectFirst();
            }

            deleteButton.setOnAction(event -> handleDelete());
            modifyButton.setOnAction(event -> handleModify());
            ajouterButton.setOnAction(event -> handleAjouter());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displaySalleDetails(Salle salle) {
        if (salle == null) return;

        nomLabel.setText(salle.getNom_salle());
        placesLabel.setText(String.valueOf(salle.getNbr_places()));
        typeLabel.setText(salle.getType_salle());
        etatLabel.setText(salle.getEtat_salle());

        // Charger l'image à partir de la base de données
        try {
            if (salle.getImage_salle() != null && !salle.getImage_salle().isEmpty()) {
                String imagePath = salle.getImage_salle();  // Le chemin enregistré en BDD

                Image image;

                // Si l'image commence déjà par "http" ou "file:/", on l'utilise directement
                if (imagePath.startsWith("http") || imagePath.startsWith("file:/")) {
                    image = new Image(imagePath, true);
                } else {
                    // Sinon, on ajoute "file:" pour charger depuis le système de fichiers
                    image = new Image("file:" + imagePath, true);
                }

                // Listener pour détecter les erreurs de chargement
                image.errorProperty().addListener((obs, oldError, newError) -> {
                    if (newError) {
                        salleImageView.setImage(null);
                        System.err.println("Erreur lors du chargement de l'image de salle: " + imagePath);
                    }
                });

                salleImageView.setImage(image);
            } else {
                salleImageView.setImage(null); // Aucun chemin
            }
        } catch (Exception e) {
            salleImageView.setImage(null);
            System.err.println("Erreur de chargement de l'image de salle: " + e.getMessage());
        }


        // Charger la description des films associés
        StringBuilder description = new StringBuilder();
        description.append("<html><body style='font-family:sans-serif;'>");
        description.append("<h3>Films associés :</h3><ul>");

        try {
            Set<Film> filmsSet = salle.getFilms();
            List<Film> films = new ArrayList<>(filmsSet);
            for (Film film : films) {
                description.append("<li>").append(film.getTitre()).append("</li>");
            }
        } catch (Exception e) {
            description.append("<li>Aucun film associé.</li>");
        }

        description.append("</ul></body></html>");

        WebEngine webEngine = descriptionWebView.getEngine();
        webEngine.loadContent(description.toString());
    }
    private void handleDelete() {
        Salle selectedSalle = salleListView.getSelectionModel().getSelectedItem();
        if (selectedSalle == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette salle ?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            salleService.supprimer(selectedSalle.getId());
            salleListView.getItems().remove(selectedSalle);
            clearDetails();
        }
    }

    private void handleModify() {
        Salle selectedSalle = salleListView.getSelectionModel().getSelectedItem();
        if (selectedSalle == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSalle.fxml"));
            Parent root = loader.load();
            ModifierSalleController controller = loader.getController();
            controller.setSalle(selectedSalle);

            Stage stage = new Stage();
            stage.setTitle("Modifier Salle");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            List<Salle> updatedList = salleService.recuperer();
            salleListView.setItems(FXCollections.observableArrayList(updatedList));
            salleListView.getSelectionModel().selectFirst();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutSalle.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Salle");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Rafraîchir la liste après l'ajout
            List<Salle> updatedList = salleService.recuperer();
            salleListView.setItems(FXCollections.observableArrayList(updatedList));
            salleListView.getSelectionModel().selectFirst();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearDetails() {
        nomLabel.setText("");
        placesLabel.setText("");
        typeLabel.setText("");
        etatLabel.setText("");
        salleImageView.setImage(null);
        descriptionWebView.getEngine().loadContent("");
    }

    @FXML
    private void handleHoverIn() {
        modifyButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20;");
    }

    @FXML
    private void handleHoverOut() {
        modifyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20;");
    }

}
