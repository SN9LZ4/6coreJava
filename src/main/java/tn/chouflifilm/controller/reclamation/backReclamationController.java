package tn.chouflifilm.controller.reclamation;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import tn.chouflifilm.controller.user.navbarController;
import tn.chouflifilm.entities.Reclamation;

import javafx.scene.control.TableView;
import tn.chouflifilm.entities.User;
import tn.chouflifilm.entities.ressource;
import tn.chouflifilm.services.Reclamation.serviceReclamation;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class backReclamationController {
    @FXML
    private Circle circle1;
    @FXML
    private TableView<Reclamation> tableView;
    @FXML
    private TableColumn<Reclamation, String> nomColumn;
    @FXML
    private TableColumn<Reclamation, String> emailColumn;
    @FXML
    private TableColumn<Reclamation, String> prenomColumn;

    @FXML
    private TableColumn<Reclamation, Void> actionsColumn;
    @FXML
    private TableColumn<Reclamation, Void> actionsColumn1;
    @FXML
    private TableColumn<Reclamation, Void> actionsColumnactions;

    serviceReclamation serviceReclamation = new serviceReclamation();

    @FXML
    public void initialize() throws SQLException {
        List<Reclamation> list = serviceReclamation.getListReclamations();
        ObservableList<Reclamation> observableList = FXCollections.observableArrayList(list);
        tableView.setItems(observableList);
        nomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getNom())
        );
        prenomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getPrenom())
        );
        emailColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getEmail()
                ));

        actionsColumn.setCellFactory(column -> new TableCell<Reclamation, Void>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(75);
                imageView.setFitWidth(60);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Reclamation res = getTableRow().getItem();
                    String imagePath = res.getImage();

                    try {
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
                        setGraphic(null);
                    }
                }
            }
        });

        actionsColumn1.setCellFactory(column -> new TableCell<Reclamation, Void>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(75);
                imageView.setFitWidth(60);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Reclamation res = getTableRow().getItem();
                    String imagePath = res.getUser().getimage();
                    System.out.println(res.getUser());

                    try {
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
                        setGraphic(null);
                    }
                }
            }
        });
        actionsColumnactions.setCellFactory(param -> new TableCell<Reclamation, Void>() {
            private final Button updateButton = new Button("\uD83D\uDD04");
            private final Button supprimerButton = new Button("❌");


            private final HBox pane = new HBox(10, updateButton, supprimerButton);

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");
                    supprimerButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");



                    setGraphic(pane);

                }
            }

        });



    }












    public void pagedashboard() throws IOException {

        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("user.fxml", currentStage);
    }
    public void pageuser() throws IOException {

        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("backback.fxml", currentStage);
    }
    public void pageAssociation() throws IOException {

        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("/Benevolat/Association/affichageBack.fxml", currentStage);
    }
}
