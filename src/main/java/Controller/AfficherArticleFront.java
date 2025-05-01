package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import tn.esprit.entities.article;
import tn.esprit.services.ArticleService;
import tn.esprit.services.ServiceException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AfficherArticleFront implements Initializable {

    @FXML
    private FlowPane flowPane;

    @FXML
    private ImageView logoImage;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryFilter;

    private final ArticleService articleService = new ArticleService();
    private List<article> allArticles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize logo
            logoImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/logo.png")).toExternalForm()));

            // Initialize category filter
            categoryFilter.getItems().addAll("Tous", "film", "auteur", "cinema", "snacks", "autre");
            categoryFilter.setValue("Tous");

            // Add listeners for real-time filtering
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterArticles());
            categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterArticles());

            // Load initial articles
            loadArticles();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadArticles() {
        try {
            allArticles = articleService.recuperer();
            filterArticles();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        filterArticles();
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        categoryFilter.setValue("Tous");
        filterArticles();
    }

    private void filterArticles() {
        if (allArticles == null) return;

        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getValue();

        List<article> filteredArticles = allArticles.stream()
            .filter(a -> {
                boolean matchesSearch = searchText.isEmpty() ||
                    a.getTitre().toLowerCase().contains(searchText) ||
                    a.getContenu().toLowerCase().contains(searchText);

                boolean matchesCategory = selectedCategory.equals("Tous") ||
                    a.getCategorie().equals(selectedCategory);

                return matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());

        displayFilteredArticles(filteredArticles);
    }

    private void displayFilteredArticles(List<article> articles) {
        flowPane.getChildren().clear();
        for (article a : articles) {
            VBox card = createArticleCard(a);
            flowPane.getChildren().add(card);
        }
    }

    private VBox createArticleCard(article a) {
        VBox card = new VBox(15);
        card.getStyleClass().add("article-card");
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.setPadding(new  Insets(15));

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);
        File file = new File("src/main/resources/images/" + a.getImage());
        if (file.exists()) {
            imageView.setImage(new Image(file.toURI().toString()));
        }
        
        // Catégorie
        Label categoryLabel = new Label(a.getCategorie().toUpperCase());
        categoryLabel.setStyle("-fx-background-color: #281bd815; -fx-text-fill: #281bd8; " +
                             "-fx-padding: 5 10; -fx-background-radius: 4; " +
                             "-fx-font-size: 12px; -fx-font-weight: bold;");

        // Titre
        Label titleLabel = new Label(a.getTitre());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");

        // Description
        Label descLabel = new Label(a.getContenu());
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72;");

        // Bouton
        Button btnVoirPlus = new Button("Voir plus");
        btnVoirPlus.setStyle("-fx-background-color: #281bd8; -fx-text-fill: white; " +
                             "-fx-font-weight: bold; -fx-background-radius: 6; " +
                             "-fx-padding: 8 16; -fx-cursor: hand;");
        btnVoirPlus.setOnAction(e -> openDetailArticle(a));

        card.getChildren().addAll(imageView, categoryLabel, titleLabel, descLabel, btnVoirPlus);
        return card;
    }

    private void openDetailArticle(article selectedArticle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailArticle.fxml"));
            Parent root = loader.load();

            // Passage des données au contrôleur
            DetailArticle controller = loader.getController();
            controller.setArticle(selectedArticle);

            Stage stage = new Stage();
            stage.setTitle("Détail de l’article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(ActionEvent actionEvent) {

    }
}
