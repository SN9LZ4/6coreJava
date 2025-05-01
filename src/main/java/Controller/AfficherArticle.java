package Controller;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.article;
import tn.esprit.services.ArticleService;
import tn.esprit.services.ServiceException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.itextpdf.text.DocumentException;

public class AfficherArticle {

    @FXML
    private VBox articleCardContainer;

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
            showAlert(Alert.AlertType.ERROR, "Erreur d'ouverture de la page AjouterArticle.");
        }
    }

    @FXML
    public void initialize() {
        loadArticleCards();
        updateStatistics();
    }

    private void loadArticleCards() {
        try {
            List<article> articles = articleService.recuperer();
            articleCardContainer.getChildren().clear();

            for (article a : articles) {
                HBox card = new HBox(20);
                card.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
                card.setPrefHeight(120);

                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(80);
                File file = new File("src/main/resources/images/" + a.getImage());
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                }

                VBox infoBox = new VBox(5);
                Label title = new Label(a.getTitre());
                title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
                Label category = new Label("Catégorie : " + a.getCategorie());
                Label date = new Label("Date : " + a.getDate_publication());
                Label content = new Label(a.getContenu());
                content.setWrapText(true);
                content.setMaxWidth(300);
                infoBox.getChildren().addAll(title, category, date, content);

                HBox actionIcons = new HBox(10);
                actionIcons.setStyle("-fx-alignment: center-right;");
                actionIcons.setPrefWidth(Region.USE_COMPUTED_SIZE);

                ImageView statsIcon = createIcon("stats.png", "Statistiques", e -> openArticleStats(a));
                ImageView commentIcon = createIcon("comments.png", "Commentaires", e -> openCommentaires(a));
                ImageView editIcon = createIcon("edit.png", "Modifier", e -> openEditerArticle(a));
                ImageView deleteIcon = createIcon("delete.png", "Supprimer", e -> supprimerArticle(a));

                actionIcons.getChildren().addAll(statsIcon, commentIcon, editIcon, deleteIcon);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                card.getChildren().addAll(imageView, infoBox, spacer, actionIcons);
                articleCardContainer.getChildren().add(card);
            }
        } catch (ServiceException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement des articles : " + e.getMessage());
        }
    }

    private ImageView createIcon(String iconName, String tooltip, EventHandler<MouseEvent> handler) {
        ImageView imageView = new ImageView();
        try {
            String imagePath = "/images/" + iconName;
            URL resourceUrl = getClass().getResource(imagePath);
            if (resourceUrl == null) {
                System.err.println("Cannot find resource: " + imagePath);
                return imageView;
            }
            Image image = new Image(((java.net.URL) resourceUrl).toExternalForm());
            imageView.setImage(image);
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
            imageView.setStyle("-fx-cursor: hand;");
            Tooltip.install(imageView, new Tooltip(tooltip));
            imageView.setOnMouseClicked(handler);
        } catch (Exception e) {
            System.err.println("Error loading icon " + iconName + ": " + e.getMessage());
        }
        return imageView;
    }

    private void openCommentaires(article a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCommentaires.fxml"));
            Parent root = loader.load();
            Controller.ListeCommentaires controller = loader.getController();
            controller.setArticle(a);
            Stage stage = new Stage();
            stage.setTitle("Commentaires");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l’ouverture des commentaires.");
        }
    }

    private void openEditerArticle(article a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierArticle.fxml"));
            Parent root = loader.load();
            ModifierArticle controller = loader.getController();
            controller.setArticle(a);
            Stage stage = new Stage();
            stage.setTitle("Modifier l’article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l’ouverture du formulaire de modification.");
        }
    }

    private void supprimerArticle(article a) {
        try {
            articleService.remove(a);
            loadArticleCards(); // Refresh list after delete
            showAlert(Alert.AlertType.INFORMATION, "Article supprimé avec succès !");
        } catch (ServiceException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression.");
        }
    }

    private void openArticleStats(article a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ArticleStats.fxml"));
            Parent root = loader.load();

            ArticleStatsController controller = loader.getController();
            controller.setArticle(a);

            Stage stage = new Stage();
            stage.setTitle("Statistiques de l'article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture des statistiques.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Liste des articles");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private Label avgRatingLabel;
    @FXML private Label totalRatingsLabel;
    @FXML private Label totalLikesLabel;
    @FXML private Label totalDislikesLabel;
    @FXML private BarChart<String, Number> ratingDistributionChart;
    @FXML private PieChart reactionsPieChart;
    @FXML
    private Button exportStatsPdfButton;

    private void updateStatistics() {
        try {
            // Calculate statistics from all articles
            List<article> articles = articleService.recuperer();
            
            // Calculate average rating
            double totalRating = 0;
            int totalRatingCount = 0;
            Map<Integer, Integer> ratingDistribution = new HashMap<>();
            int totalLikes = 0;
            int totalDislikes = 0;

            for (article a : articles) {
                // Ratings
                if (a.getRating_count() > 0) {
                    totalRating += a.getTotal_rating() * a.getRating_count();
                    totalRatingCount += a.getRating_count();
                    
                    // Add to distribution (rounded rating)
                    int roundedRating = (int) Math.round(a.getTotal_rating());
                    ratingDistribution.merge(roundedRating, a.getRating_count(), Integer::sum);
                }
                
                // Reactions
                totalLikes += a.getLikes();
                totalDislikes += a.getDislikes();
            }

            // Update statistics labels
            double avgRating = totalRatingCount > 0 ? totalRating / totalRatingCount : 0;
            avgRatingLabel.setText(String.format("%.1f", avgRating));
            totalRatingsLabel.setText("Total: " + totalRatingCount + " notes");
            totalLikesLabel.setText(String.format("%,d", totalLikes));
            totalDislikesLabel.setText(String.format("%,d", totalDislikes));

            // Update rating distribution chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int i = 1; i <= 5; i++) {
                series.getData().add(new XYChart.Data<>(String.valueOf(i), 
                    ratingDistribution.getOrDefault(i, 0)));
            }
            ratingDistributionChart.getData().clear();
            ratingDistributionChart.getData().add(series);

            // Update reactions pie chart
            reactionsPieChart.getData().clear();
            reactionsPieChart.getData().addAll(
                new PieChart.Data("J'aime", totalLikes),
                new PieChart.Data("Je n'aime pas", totalDislikes)
            );

        } catch (ServiceException e) {
            e.printStackTrace();
            // Handle error appropriately
        }
    }

    @FXML
    private void exportAllArticlesStatsToPdf() {
        try {
            List<article> articles = articleService.recuperer();
            PDFGenerator.generateAllArticlesStatsPDF(articles);
            showAlert(Alert.AlertType.INFORMATION, "Le rapport PDF a été généré avec succès et enregistré dans votre dossier Téléchargements.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
}
