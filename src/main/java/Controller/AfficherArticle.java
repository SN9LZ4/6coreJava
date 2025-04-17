package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.article;
import tn.esprit.services.ArticleService;
import tn.esprit.services.ServiceException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AfficherArticle {

    @FXML
    private TableView<article> articleTable;

    @FXML
    private TableColumn<article, String> nomColumn;

    @FXML
    private TableColumn<article, String> categorieColumn;

    @FXML
    private TableColumn<article, java.util.Date> dateColumn;

    @FXML
    private TableColumn<article, String> contenuColumn;

    @FXML
    private TableColumn<article, String> imageColumn;

    @FXML
    private TableColumn<article, Void> actionsColumn;

    @FXML
    private TableColumn<article, Void> commentaireColumn;

    private final ArticleService articleService = new ArticleService();
    @FXML
    private void ouvrirAjouterArticle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterArticle.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur d'ouverture de la page AjouterArticle.");
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_publication"));
        contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu"));

        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(60);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    File file = new File("src/main/resources/images/" + imagePath);
                    if (file.exists()) {
                        imageView.setImage(new Image(file.toURI().toString()));
                        setGraphic(imageView);
                    } else {
                        setGraphic(new Label("Image introuvable"));
                    }
                }
            }
        });

        commentaireColumn.setCellFactory(column -> new TableCell<>() {
            private final Button btnCommentaires = new Button("Voir commentaires");

            {
                btnCommentaires.setStyle("-fx-background-color: #ffb300; -fx-text-fill: black; -fx-font-weight: bold;");
                btnCommentaires.setTooltip(new Tooltip("Voir les commentaires de cet article"));

                btnCommentaires.setOnAction(e -> {
                    article selected = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCommentaires.fxml"));
                        Parent root = loader.load();

                        Controller.ListeCommentaires controller = loader.getController();
                        controller.setArticle(selected);

                        Stage stage = new Stage();
                        stage.setTitle("Commentaires de l’article");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Impossible d’ouvrir la fenêtre des commentaires.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnCommentaires);
            }
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox hBox = new HBox(10, btnModifier, btnSupprimer);

            {
                btnModifier.setStyle("-fx-background-color: #039be5; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");

                btnModifier.setTooltip(new Tooltip("Modifier cet article"));
                btnSupprimer.setTooltip(new Tooltip("Supprimer cet article"));

                hBox.setStyle("-fx-alignment: CENTER;");

                btnModifier.setOnAction(e -> {
                    article selected = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierArticle.fxml"));
                        Parent root = loader.load();

                        ModifierArticle controller = loader.getController();
                        controller.setArticle(selected);

                        Stage stage = new Stage();
                        stage.setTitle("Modifier l’article");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Impossible d’ouvrir la fenêtre de modification.");
                    }
                });

                btnSupprimer.setOnAction(e -> {
                    article selected = getTableView().getItems().get(getIndex());
                    try {
                        articleService.remove(selected);
                        articleTable.getItems().remove(selected);
                        showAlert(Alert.AlertType.INFORMATION, "Article supprimé avec succès !");
                    } catch (ServiceException ex) {
                        showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hBox);
            }
        });

        loadArticles();
    }

    private void loadArticles() {
        try {
            List<article> articles = articleService.recuperer();
            ObservableList<article> observableList = FXCollections.observableArrayList(articles);
            articleTable.setItems(observableList);
        } catch (ServiceException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement des articles : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Liste des articles");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}