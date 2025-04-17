package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.article;
import tn.esprit.services.ArticleService;
import tn.esprit.services.ServiceException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AfficherArticleFront implements Initializable {

    @FXML
    private FlowPane flowPane;

    @FXML
    private ImageView logoImage;

    private final ArticleService articleService = new ArticleService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Chargement du logo
            logoImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/logo.png")).toExternalForm()));

            // Récupération et affichage des articles
            List<article> articles = articleService.recuperer();
            for (article a : articles) {
                VBox card = createArticleCard(a);
                flowPane.getChildren().add(card);
            }

        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Logo introuvable : /images/logo.png");
            e.printStackTrace();
        }
    }

    private VBox createArticleCard(article a) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");
        vbox.setPrefWidth(280);

        // Image de l'article
        ImageView imageView = new ImageView();
        File file = new File("src/main/resources/images/" + a.getImage());
        if (file.exists()) {
            imageView.setImage(new Image(file.toURI().toString()));
        }
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Titre
        Label titleLabel = new Label(a.getTitre());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        // Catégorie
        Label catLabel = new Label("Catégorie : " + a.getCategorie());
        catLabel.setStyle("-fx-text-fill: #6c757d;");

        // Bouton Voir plus
        Button btnVoirPlus = new Button("Voir plus");
        btnVoirPlus.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnVoirPlus.setOnAction(e -> openDetailArticle(a));

        vbox.getChildren().addAll(imageView, titleLabel, catLabel, btnVoirPlus);
        return vbox;
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
}
