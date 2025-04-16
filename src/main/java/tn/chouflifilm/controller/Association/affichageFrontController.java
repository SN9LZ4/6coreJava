package tn.chouflifilm.controller.Association;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.chouflifilm.controller.user.navbarController;
import tn.chouflifilm.entities.Association;
import tn.chouflifilm.entities.Reclamation;
import tn.chouflifilm.services.Association.associationService;
import tn.chouflifilm.services.userService;
import tn.chouflifilm.tools.UserSessionManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;

public class affichageFrontController
{
    private final tn.chouflifilm.services.userService userService = new userService();
    private final tn.chouflifilm.services.Association.associationService associationService = new associationService();
    @FXML
    private ImageView imageView;
    @FXML
    private VBox reclamationContainer;


    @FXML
    public void initialize() throws SQLException {

        String imageFromDb = UserSessionManager.getInstance().getCurrentUser().getimage();
        Image image1 = new Image(imageFromDb);
        imageView.setImage(image1);

// Appliquer un clip circulaire
        Circle clipCircle = new Circle(
                imageView.getFitWidth() / 2,
                imageView.getFitHeight() / 2,
                Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2
        );
        imageView.setClip(clipCircle);


        reclamationContainer.getChildren().clear();

        Label mainTitle = new Label("ASSOCIATIONS");
        mainTitle.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-padding: 0 0 0 0;");
        mainTitle.setPrefWidth(Double.MAX_VALUE);
        mainTitle.setAlignment(Pos.CENTER);
        reclamationContainer.getChildren().add(mainTitle);

        Separator titleSeparator = new Separator();
        titleSeparator.setStyle("-fx-background-color: #3b5998;");
        reclamationContainer.getChildren().add(titleSeparator);

        Region spacer = new Region();
        spacer.setPrefHeight(15);
        reclamationContainer.getChildren().add(spacer);

        VBox listContainer = new VBox(10);

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefViewportHeight(350);
        scrollPane.getStyleClass().add("edge-to-edge");

        List<Association> list = associationService.afficherAssociations();
        if (list.isEmpty()) {
            Label emptyLabel = new Label("Aucune réclamation disponible");
            emptyLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #666;");
            emptyLabel.setAlignment(Pos.CENTER);
            listContainer.getChildren().add(emptyLabel);
        } else {
            for (Association association : list) {
                VBox itemContainer = new VBox(10);
                itemContainer.setStyle("-fx-background-color: #3b5998; -fx-padding: 15; -fx-background-radius: 10;");
                itemContainer.setEffect(new DropShadow());

                HBox contentLayout = new HBox(15);
                contentLayout.setAlignment(Pos.CENTER_LEFT);

                ImageView imageView = new ImageView();
                imageView.setFitHeight(140);
                imageView.setFitWidth(140);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-background-radius: 8;");
                imageView.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));

                Image image;
                try {
                    String imageName = association.getImage();
                    if (imageName != null && !imageName.isEmpty()) {
                        image = new Image(getClass().getResourceAsStream(imageName));
                        if (image.isError()) throw new Exception("Image not found");
                        imageView.setImage(image);
                    } else {
                        throw new Exception("No image name provided");
                    }
                } catch (Exception e) {
                    image = new Image(getClass().getResourceAsStream("/images/inconnu.jpg"));
                    imageView.setImage(image);
                }

                Region spaceBetween = new Region();
                spaceBetween.setPrefWidth(60);

                VBox infoBox = new VBox(12);
                infoBox.setPadding(new Insets(5, 0, 5, 0));

                String labelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #E0E6F8;";
                String valueStyle = "-fx-font-size: 16px; -fx-text-fill: white;";

                // Ajout des champs de réclamation
                infoBox.getChildren().add(createInfoRow("Description:", new Text(association.getDescription()), labelStyle, valueStyle));
                infoBox.getChildren().add(createInfoRow("Status:", new Label(association.getNom()), labelStyle, valueStyle));
                infoBox.getChildren().add(createInfoRow("Type:", new Label(association.getAdresse()), labelStyle, valueStyle));
                infoBox.getChildren().add(createInfoRow("Temps:", new Label(association.getMail_association()), labelStyle, valueStyle));

                // Création de boutons modifier et supprimer
                HBox actionButtons = new HBox(15);
                actionButtons.setAlignment(Pos.CENTER);


                Button deleteButton = new Button("Aider");
                deleteButton.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;");
                deleteButton.setOnAction(e -> {
                    try {
                        System.out.println(association.getId());
                      UserSessionManager.getInstance().setAssociation(association);
                        this.pageressource();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                actionButtons.getChildren().addAll( deleteButton);

                // Ajouter tout à la mise en page
                contentLayout.getChildren().addAll(imageView, spaceBetween, infoBox);
                itemContainer.getChildren().add(contentLayout);
                itemContainer.getChildren().add(actionButtons);
                listContainer.getChildren().add(itemContainer);
            }
        }
        reclamationContainer.getChildren().add(scrollPane);
    }
        private HBox createInfoRow(String labelText, javafx.scene.Node valueNode, String labelStyle, String valueStyle) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label label = new Label(labelText);
            label.setStyle(labelStyle);

            if (valueNode instanceof Label)
                ((Label) valueNode).setStyle(valueStyle);
            else if (valueNode instanceof Text) {
                ((Text) valueNode).setStyle(valueStyle);
                ((Text) valueNode).setWrappingWidth(280);
            }

            row.getChildren().addAll(label, valueNode);
            return row;
        }
        private String formatDate(Date date) {
            if (date == null) return "N/A";
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
            return formatter.format(date);
        }


    public void pageressource() throws IOException {

        Stage currentStage = (Stage) imageView.getScene().getWindow();
        navbarController.changeScene("/Benevolat/Ressource/addRessourceFront.fxml", currentStage);
    }







}
