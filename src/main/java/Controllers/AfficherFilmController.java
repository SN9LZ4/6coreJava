package Controllers;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tn.esprit.entities.Film;
import tn.esprit.services.FilmService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class AfficherFilmController {

    @FXML private ListView<Film> rlist;
    @FXML private Label titreLabel, directeurLabel, noteLabel, genreLabel, dureeLabel, dateDebutLabel, dateFinLabel;
    @FXML private ImageView filmImageView;
    @FXML private WebView descriptionWebView;
    @FXML private Button modifyButton, deleteButton;
    @FXML private TextField titreField, directeurField, genreField, imageField, noteField, dureeField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateDebutField, dateFinField;
    @FXML private Button btnSave;

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

    @FXML
    private void openPowerBIInBrowser() {
        try {
            // URL Power BI
            String powerBiUrl = "https://app.powerbi.com/reportEmbed?" +
                    "reportId=e606ed65-4c93-48d6-85c7-aa23ee405b90&" +
                    "autoAuth=true&" +
                    "ctid=604f1a96-cbe8-43f8-abbf-f8eaf5d85730";

            // Solution multi-plateforme pour ouvrir le navigateur
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(new URI(powerBiUrl));
            } else {
                // Fallback pour les systèmes non supportés
                Runtime runtime = Runtime.getRuntime();
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + powerBiUrl);
                } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    runtime.exec("open " + powerBiUrl);
                } else {
                    runtime.exec("xdg-open " + powerBiUrl);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ouverture du navigateur: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Impossible d'ouvrir le navigateur: " + e.getMessage());
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
            titreLabel.setText(film.getTitre() != null ? film.getTitre() : "Non spécifié");
            directeurLabel.setText(film.getDirecteur() != null ? film.getDirecteur() : "Non spécifié");
            genreLabel.setText(film.getGenre() != null ? film.getGenre() : "Non spécifié");
            noteLabel.setText(film.getNote() > 0 ? String.format("%.1f/10", film.getNote()) : "Non noté");
            dureeLabel.setText(film.getDuree() > 0 ? film.getDuree() + " min" : "Durée non spécifiée");

            try {
                dateDebutLabel.setText(film.getDateDebut() != null ?
                        dateFormat.format(film.getDateDebut()) : "Non spécifiée");
                dateFinLabel.setText(film.getDateFin() != null ?
                        dateFormat.format(film.getDateFin()) : "Non spécifiée");
            } catch (Exception e) {
                dateDebutLabel.setText("Format date invalide");
                dateFinLabel.setText("Format date invalide");
            }

            String descriptionHtml = "<html><body style='font-family:sans-serif; padding:10px;'>" +
                    (film.getDescription() != null ? film.getDescription() : "Aucune description disponible") +
                    "</body></html>";
            descriptionWebView.getEngine().loadContent(descriptionHtml);

            try {
                if (film.getImage() != null && !film.getImage().isEmpty()) {
                    String imagePath = film.getImage();
                    Image img = imagePath.startsWith("http") || imagePath.startsWith("file:/") ?
                            new Image(imagePath, true) : new Image("file:" + imagePath, true);

                    img.errorProperty().addListener((obs, wasError, isNowError) -> {
                        if (isNowError) filmImageView.setImage(null);
                    });

                    filmImageView.setImage(img);
                } else {
                    filmImageView.setImage(null);
                }
            } catch (Exception e) {
                filmImageView.setImage(null);
            }
        });
    }

    @FXML
    public void handleRetour(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/AfficherSalle.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
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

    private void showAlert(Alert.AlertType type, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void refreshFilmList() {
        try {
            loadFilms();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement: " + e.getMessage());
        }
    }

    @FXML
    private void handleModify(ActionEvent event) {
        Film selectedFilm = rlist.getSelectionModel().getSelectedItem();
        if (selectedFilm == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un film à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFilm.fxml"));
            Parent root = loader.load();
            ModifierFilmController controller = loader.getController();
            controller.setFilm(selectedFilm);

            Stage stage = new Stage();
            stage.setTitle("Modifier Film");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshFilmList();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la fenêtre de modification.");
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

        if (alert.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
            try {
                filmService.supprimer(selectedFilm);
                rlist.getItems().remove(selectedFilm);
                afficherFilm(null);
                showAlert("Succès", "Le film a été supprimé avec succès.");
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAjouterFilm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ajouterFilm.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleSave() {
        try {
            if (titreField.getText().isEmpty() || genreField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
                return;
            }

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

            if (dateDebutField.getValue() == null || dateFinField.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Les dates doivent être spécifiées.");
                return;
            }

            if (dateFinField.getValue().isBefore(dateDebutField.getValue())) {
                showAlert(Alert.AlertType.ERROR, "La date de fin ne peut pas être antérieure à la date de début.");
                return;
            }

            film.setTitre(titreField.getText());
            film.setDirecteur(directeurField.getText());
            film.setGenre(genreField.getText());
            film.setDescription(descriptionField.getText());
            film.setNote(note);
            film.setDuree(duree);
            film.setDateDebut(Date.valueOf(dateDebutField.getValue()));
            film.setDateFin(Date.valueOf(dateFinField.getValue()));
            film.setImage(imageField.getText());

            filmService.edit(film);
            showAlert(Alert.AlertType.INFORMATION, "Film modifié avec succès.");
            ((Stage) btnSave.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification : " + e.getMessage());
        }
    }
    @FXML
    private void handleExportToExcel(ActionEvent event) {
        try {
            // Créer un nouveau classeur Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Liste des Films");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Titre", "Directeur", "Genre", "Note", "Durée (min)", "Date Début", "Date Fin", "Description"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = (Cell) headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Remplir les données
            List<Film> films = rlist.getItems();
            int rowNum = 1;

            for (Film film : films) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(film.getTitre());
                row.createCell(1).setCellValue(film.getDirecteur());
                row.createCell(2).setCellValue(film.getGenre());
                row.createCell(3).setCellValue(film.getNote());
                row.createCell(4).setCellValue(film.getDuree());
                row.createCell(5).setCellValue(film.getDateDebut() != null ? dateFormat.format(film.getDateDebut()) : "");
                row.createCell(6).setCellValue(film.getDateFin() != null ? dateFormat.format(film.getDateFin()) : "");
                row.createCell(7).setCellValue(film.getDescription());
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Demander à l'utilisateur où enregistrer le fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
            fileChooser.setInitialFileName("films_export.xlsx");

            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

            if (file != null) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                    showAlert("Succès", "Exportation réussie vers: " + file.getAbsolutePath());
                }
            }

            workbook.close();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'exportation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}