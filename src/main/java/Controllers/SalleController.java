package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Film;
import tn.esprit.entities.Salle;
import tn.esprit.services.FilmService;
import tn.esprit.services.SalleService;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SalleController {

    @FXML private TextField nomSalleField;
    @FXML private Spinner<Integer> nbrPlacesSpinner;
    @FXML private ComboBox<String> typeSalleCombo;
    @FXML private ComboBox<String> etatSalleCombo;
    @FXML private TextField imageSalleField;
    @FXML private ListView<Film> filmListView;
    @FXML private Button ajouterSalleButton;
    @FXML private ImageView salleImageView;

    private final SalleService salleService = new SalleService();
    private final FilmService filmService = new FilmService();

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500, 100);
        nbrPlacesSpinner.setValueFactory(valueFactory);

        typeSalleCombo.setItems(FXCollections.observableArrayList("3D", "IMAX", "Classique", "VIP"));
        etatSalleCombo.setItems(FXCollections.observableArrayList("Disponible", "Indisponible", "Maintenance"));

        try {
            List<Film> films = filmService.recuperer();
            ObservableList<Film> filmList = FXCollections.observableArrayList(films);
            filmListView.setItems(filmList);
            filmListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            filmListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Film film, boolean empty) {
                    super.updateItem(film, empty);
                    if (empty || film == null) {
                        setText(null);
                    } else {
                        setText(film.getTitre() + " - " + film.getGenre() + " (" + film.getDuree() + ")");
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des films : " + e.getMessage());
        }
    }

    @FXML
    void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image pour la salle");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", ".png", ".jpg", ".jpeg", ".gif")
        );

        File file = fileChooser.showOpenDialog(nomSalleField.getScene().getWindow());
        if (file != null) {
            String imagePath = file.toURI().toString();
            imageSalleField.setText(imagePath);
            salleImageView.setImage(new Image(imagePath));
        }
    }

    @FXML
    void ajouterSalle(ActionEvent event) {
        String nom = nomSalleField.getText().trim();
        String type = typeSalleCombo.getValue();
        String etat = etatSalleCombo.getValue();
        String imagePath = imageSalleField.getText().trim();

        if (nom.isEmpty() || type == null || etat == null || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Tous les champs doivent être remplis.");
            return;
        }

        if (!nom.matches("^[a-zA-ZÀ-ÿ\\s\\-\\.']{3,50}$")) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Le nom de la salle doit contenir uniquement des lettres (3-100 caractères).");
            return;
        }

        try {
            Salle salle = new Salle();
            salle.setNom_salle(nom);
            salle.setNbr_places(nbrPlacesSpinner.getValue());
            salle.setType_salle(type);
            salle.setEtat_salle(etat);
            salle.setImage_salle(imagePath);

            Set<Film> selectedFilms = new HashSet<>(filmListView.getSelectionModel().getSelectedItems());
            salle.setFilms(selectedFilms);

            salleService.ajouter(salle);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherSalle.fxml"));
            Parent root = loader.load();
            nomSalleField.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ajout de la salle : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}