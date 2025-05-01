package Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tn.esprit.entities.article;
import tn.esprit.entities.commentaire;
import tn.esprit.services.ServiceException;
import tn.esprit.services.commentaireService;

import java.util.List;

public class ListeCommentaires {

    @FXML
    private VBox commentaireContainer;

    private final commentaireService commentaireService = new commentaireService();

    private article currentArticle;

    public void setArticle(article a) {
        this.currentArticle = a;
        afficherCommentaires();
    }

    public void afficherCommentaires() {
        commentaireContainer.getChildren().clear();
        try {
            List<commentaire> commentaires = commentaireService.getCommentairesByArticle(currentArticle);
            for (commentaire c : commentaires) {
                commentaireContainer.getChildren().add(createCommentCard(c));
            }
        } catch (ServiceException e) {
            showAlert("Erreur de chargement des commentaires : " + e.getMessage());
        }
    }

    private HBox createCommentCard(commentaire c) {
        // User full name
        String nomComplet = c.getUser().getNom() + " " + c.getUser().getPrenom();

        Label nomLabel = new Label(nomComplet);
        nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label contenuLabel = new Label(c.getContenuCom());
        contenuLabel.setWrapText(true);
        contenuLabel.setMaxWidth(400);
        contenuLabel.setStyle("-fx-text-fill: #333;");

        VBox contentBox = new VBox(5, nomLabel, contenuLabel);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");
        deleteBtn.setOnAction(event -> {
            try {
                commentaireService.delete(c, c.getUser()); // adapt if needed
                commentaireContainer.getChildren().removeIf(node -> node == deleteBtn.getParent());
            } catch (ServiceException e) {
                showAlert("Erreur lors de la suppression du commentaire.");
            }
        });

        HBox card = new HBox(20, contentBox, deleteBtn);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        return card;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
