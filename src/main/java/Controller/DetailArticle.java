package Controller;
import javafx.event.ActionEvent; // ✅ C'est celui qu'il faut

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.entities.article;
import tn.esprit.entities.commentaire;
import tn.esprit.services.commentaireService;
import tn.esprit.services.ServiceException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailArticle {

    @FXML
    private Label titleLabel;

    @FXML
    private Label categorieLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label contenuLabel;

    @FXML
    private ImageView articleImage;

    @FXML
    private TextArea commentaireTextArea;

    @FXML
    private VBox commentairesContainer;

    private final commentaireService commentaireService = new commentaireService();

    private article currentArticle;

    private User currentUser = new User(32, "nom", "prenom", false); // À remplacer par la session réelle

    public void setArticle(article a) {
        this.currentArticle = a;
        titleLabel.setText(a.getTitre());
        categorieLabel.setText("Catégorie : " + a.getCategorie());
        dateLabel.setText("Date de publication : " + new SimpleDateFormat("dd MMM yyyy").format(a.getDate_publication()));
        contenuLabel.setText(a.getContenu());

        File file = new File("src/main/resources/images/" + a.getImage());
        if (file.exists()) {
            articleImage.setImage(new Image(file.toURI().toString()));
        }

        afficherCommentaires();
    }

    @FXML
    private void handleComment() {
        String contenu = commentaireTextArea.getText().trim();

        if (contenu.isEmpty()) {
            showAlert("❌ Le commentaire ne peut pas être vide.");
            return;
        }
        if (contenu.matches(".*(spam|insulte1|insulte2).*")) {
            showAlert("❌ Le commentaire contient un mot inapproprié.");
            return;
        }
        if (!contenu.matches(".*[a-zA-ZÀ-ÿ].*")) {
            showAlert("❌ Le commentaire doit contenir du texte significatif.");
            return;
        }

        if (contenu.length() < 5) {
            showAlert("❌ Le commentaire doit contenir au moins 5 caractères.");
            return;
        }

        if (contenu.length() > 100) {
            showAlert("❌ Le commentaire ne doit pas dépasser 100 caractères.");
            return;
        }

        if (currentUser == null) {
            showAlert("Veuillez vous connecter pour commenter.");
            return;
        }

        commentaire nouveauCommentaire = new commentaire();
        nouveauCommentaire.setContenuCom(contenu);
        nouveauCommentaire.setArticle(currentArticle);
        nouveauCommentaire.setUser(currentUser);
        nouveauCommentaire.setDate(new Date());

        try {
            commentaireService.add(nouveauCommentaire);
            commentaireTextArea.clear();
            afficherCommentaires();
        } catch (ServiceException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l’ajout du commentaire.");
        }
    }


    private void afficherCommentaires() {
        commentairesContainer.getChildren().clear();
        try {
            List<commentaire> commentaires = commentaireService.getCommentairesByArticle(currentArticle);
            for (commentaire com : commentaires) {
                VBox comBox = new VBox(5);
                comBox.setStyle("-fx-background-color: #f1f1f1; -fx-padding: 10; -fx-border-radius: 8; -fx-background-radius: 8;");

                Label userLabel = new Label(com.getUser().getNom() + " " + com.getUser().getPrenom());
                userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

                Label contenuLabel = new Label(com.getContenuCom());
                contenuLabel.setWrapText(true);
                contenuLabel.setStyle("-fx-text-fill: #333333;");

                HBox actions = new HBox(10);
                actions.setStyle("-fx-alignment: center-right;");

                if (currentUser != null && (currentUser.getUser_id() == com.getUser().getUser_id() || currentUser.isAdmin())) {
                    ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
                    editIcon.setFitWidth(20);
                    editIcon.setFitHeight(20);
                    Button btnEdit = new Button("", editIcon);
                    btnEdit.setStyle("-fx-background-color: transparent;");
                    btnEdit.setOnAction(e -> {
                        commentaireTextArea.setText(com.getContenuCom());
                        try {
                            commentaireService.delete(com, currentUser);
                        } catch (ServiceException ex) {
                            showAlert("Erreur lors de la préparation à l’édition.");
                        }
                    });

                    ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                    deleteIcon.setFitWidth(20);
                    deleteIcon.setFitHeight(20);
                    Button btnDelete = new Button("", deleteIcon);
                    btnDelete.setStyle("-fx-background-color: transparent;");
                    btnDelete.setOnAction(e -> {
                        try {
                            commentaireService.delete(com, currentUser);
                            afficherCommentaires();
                        } catch (ServiceException ex) {
                            showAlert("Erreur lors de la suppression.");
                        }
                    });

                    ImageView likeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/like.png")));
                    likeIcon.setFitWidth(20);
                    likeIcon.setFitHeight(20);
                    Button btnLike = new Button("", likeIcon);
                    btnLike.setStyle("-fx-background-color: transparent;");

                    actions.getChildren().addAll(btnLike, btnEdit, btnDelete);
                }

                comBox.getChildren().addAll(userLabel, contenuLabel, actions);
                commentairesContainer.getChildren().add(comBox);
            }
        } catch (ServiceException e) {
            showAlert("Erreur de chargement des commentaires.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticleFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}