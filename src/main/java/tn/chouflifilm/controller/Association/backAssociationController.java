package tn.chouflifilm.controller.Association;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;
import tn.chouflifilm.controller.Ressource.backRessourceController;
import tn.chouflifilm.controller.user.navbarController;
import tn.chouflifilm.entities.Association;
import tn.chouflifilm.entities.User;
import tn.chouflifilm.services.Association.associationService;
import tn.chouflifilm.services.userService;
import tn.chouflifilm.tools.UserSessionManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class backAssociationController {

    @FXML
    private ImageView imageView;
    @FXML
    private Circle circle1;

    @FXML
    private Circle circle2;

    @FXML
    private Circle circle3;

    @FXML
    private Circle circle4;
    @FXML
    private Circle circle5;

    @FXML
    private Circle circle6;

    @FXML
    private Circle circle7;

    @FXML
    private Circle circle8;

    @FXML
    private Circle circle9;
    @FXML
    private Circle circle10;

    @FXML
    private Circle circle11;

    @FXML
    private Circle circle12;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Association> tableView;
@FXML
private TextField  nom;
@FXML
private TextField  email;
@FXML
private TextField  tel;
@FXML
private TextField  adresse;
@FXML
private TextArea  description;
@FXML
private Label imagePathLabel;

    @FXML
    private TableColumn<Association, Void> actionsColumn;

    private tn.chouflifilm.services.Association.associationService associationService = new associationService();

    public backAssociationController() throws SQLException {
    }
    private userService userService = new userService();

    @FXML
    public void initialize() throws SQLException {

        User user= userService.recherparid(UserSessionManager.getInstance().getCurrentUser().getId());

        String imagePath = user.getimage();
        Image image = new Image(imagePath);


        imageView.setImage(image);


        Circle clipCircle = new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2);
        imageView.setClip(clipCircle);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    pageressource(newSelection);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });



        actionsColumn.setCellFactory(param -> new TableCell<Association, Void>() {

                    private final Button updateButton = new Button("\uD83D\uDD04");
                    private final Button supprimerButton = new Button("❌");
                    private final HBox pane = new HBox(10,updateButton, supprimerButton);



            {

                updateButton.setOnAction(event -> {
                    Association association= getTableView().getItems().get(getIndex());
                    try {
                        pageedit(association);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                });


            }




            {
                supprimerButton.setOnAction(event -> {
                    Association association= getTableView().getItems().get(getIndex());
                    System.out.println(association.getMail_association());

                    try {
                        supprimerAssociation(association);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

            }



            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);  // Aucune cellule à afficher si vide
                } else {
                    updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");
                    supprimerButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");


                    setGraphic(pane);

                }}
                });




        animateBackgroundCircles();


        List<Association> associations = associationService.afficherAssociations();
        ObservableList<Association> observableUsers = FXCollections.observableArrayList(associations);
        tableView.setItems(observableUsers);

    }


    private void supprimerAssociation(Association association) throws SQLException {
        try {
System.out.println("affichage information association");
System.out.println(association.getId());
System.out.println(association.getMail_association());
           associationService.supprimerAssociation(association.getId());

            tableView.getItems().remove(association);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @FXML
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une Image de Profil");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            imagePathLabel.setText(selectedFile.getAbsolutePath()); // Affiche le chemin de l'image
        }
    }

    public void ajouterAssociation() throws SQLException {
String nomInput  = nom.getText();
String emailInput  = email.getText();
String telInput  = tel.getText();
String adresseInput  = adresse.getText();
String descriptionInput  = description.getText();
        int phoneInput = Integer.parseInt(tel.getText());
        String imagePathInput = imagePathLabel.getText();
Association association = new Association(nomInput,emailInput, adresseInput,phoneInput,imagePathInput,descriptionInput);
System.out.println(association);
try {
    associationService.ajouterAssociation(association);
    List<Association> associations = associationService.afficherAssociations();
    ObservableList<Association> observableUsers = FXCollections.observableArrayList(associations);
    tableView.setItems(observableUsers);
            nom.clear();
            email.clear();
            adresse.clear();
            description.clear();
            tel.clear();
    imagePathLabel.setText(" Aucune Image séléctionné");

        } catch (SQLException e) {
            e.printStackTrace();

        }

    }



    @FXML
    private void search() throws SQLException {

        String nomsearch = searchField.getText().trim();
        if(nomsearch.length()!=0){
            List<Association> list =  associationService.search(nomsearch);
            ObservableList<Association> observableList = FXCollections.observableArrayList(list);
            tableView.setItems(observableList);}
        else{
            List<Association> list =  associationService.afficherAssociations();
            ObservableList<Association> observableList = FXCollections.observableArrayList(list);
            tableView.setItems(observableList);
        }
        System.out.println(nomsearch);
        System.out.println(associationService.search(nomsearch).size());

    }





    public void pagedashboard() throws IOException {

        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("user.fxml", currentStage);
    }
    public void pageuser() throws IOException {

        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("backback.fxml", currentStage);
    }

    public void pageedit(Association association) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Benevolat/Association/editback.fxml"));
        Parent root = loader.load();
        editBackController controller = loader.getController();
        controller.setAssociation(association);
        Scene currentScene = circle1.getScene();
        currentScene.setRoot(root);
    }

    public void pageressource(Association association) throws IOException, SQLException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Benevolat/Ressource/ressourceBack.fxml"));
        Parent root = loader.load();
        backRessourceController controller = loader.getController();
        controller.setAssociation(association);
        Scene currentScene = circle1.getScene();
        currentScene.setRoot(root);
    }





    private void animateBackgroundCircles() {

        TranslateTransition transition1 = new TranslateTransition(Duration.seconds(3), circle1);
        transition1.setFromX(0);
        transition1.setFromY(0);
        transition1.setToX(100);
        transition1.setToY(-50);
        transition1.setAutoReverse(true);
        transition1.setCycleCount(TranslateTransition.INDEFINITE);
        transition1.play();

        // Circle 2 - Bottom left
        TranslateTransition transition2 = new TranslateTransition(Duration.seconds(4), circle2);
        transition2.setFromX(0);
        transition2.setFromY(0);
        transition2.setToX(-100);
        transition2.setToY(50);
        transition2.setAutoReverse(true);
        transition2.setCycleCount(TranslateTransition.INDEFINITE);
        transition2.play();

        // Circle 3 - Middle left
        TranslateTransition transition3 = new TranslateTransition(Duration.seconds(3), circle3);
        transition3.setFromX(200);
        transition3.setFromY(0);
        transition3.setToX(300);
        transition3.setToY(100);
        transition3.setAutoReverse(true);
        transition3.setCycleCount(TranslateTransition.INDEFINITE);
        transition3.play();

        // Circle 4 - Upper middle left
        TranslateTransition transition4 = new TranslateTransition(Duration.seconds(4), circle4);
        transition4.setFromX(300);
        transition4.setFromY(0);
        transition4.setToX(400);
        transition4.setToY(-75);
        transition4.setAutoReverse(true);
        transition4.setCycleCount(TranslateTransition.INDEFINITE);
        transition4.play();

        // Circle 5 - Far left side
        TranslateTransition transition5 = new TranslateTransition(Duration.seconds(3), circle5);
        transition5.setFromX(-100);
        transition5.setFromY(0);
        transition5.setToX(0);
        transition5.setToY(50);
        transition5.setAutoReverse(true);
        transition5.setCycleCount(TranslateTransition.INDEFINITE);
        transition5.play();

        // Circle 6 - Center left side
        TranslateTransition transition6 = new TranslateTransition(Duration.seconds(4), circle6);
        transition6.setFromX(150);
        transition6.setFromY(0);
        transition6.setToX(200);
        transition6.setToY(-50);
        transition6.setAutoReverse(true);
        transition6.setCycleCount(TranslateTransition.INDEFINITE);
        transition6.play();

        // Right side circles (x from 600 to 950)

        // Circle 7 - Top right
        TranslateTransition transition7 = new TranslateTransition(Duration.seconds(3), circle7);
        transition7.setFromX(600);
        transition7.setFromY(0);
        transition7.setToX(700);
        transition7.setToY(-50);
        transition7.setAutoReverse(true);
        transition7.setCycleCount(TranslateTransition.INDEFINITE);
        transition7.play();

        // Circle 8 - Bottom right
        TranslateTransition transition8 = new TranslateTransition(Duration.seconds(4), circle8);
        transition8.setFromX(700);
        transition8.setFromY(0);
        transition8.setToX(800);
        transition8.setToY(100);
        transition8.setAutoReverse(true);
        transition8.setCycleCount(TranslateTransition.INDEFINITE);
        transition8.play();

        // Circle 9 - Middle right
        TranslateTransition transition9 = new TranslateTransition(Duration.seconds(3), circle9);
        transition9.setFromX(800);
        transition9.setFromY(0);
        transition9.setToX(900);
        transition9.setToY(-75);
        transition9.setAutoReverse(true);
        transition9.setCycleCount(TranslateTransition.INDEFINITE);
        transition9.play();

        // Circle 10 - Upper middle right
        TranslateTransition transition10 = new TranslateTransition(Duration.seconds(4), circle10);
        transition10.setFromX(900);
        transition10.setFromY(0);
        transition10.setToX(950);
        transition10.setToY(50);
        transition10.setAutoReverse(true);
        transition10.setCycleCount(TranslateTransition.INDEFINITE);
        transition10.play();

        // Circle 11 - Far right side
        TranslateTransition transition11 = new TranslateTransition(Duration.seconds(3), circle11);
        transition11.setFromX(1000);
        transition11.setFromY(0);
        transition11.setToX(1100);
        transition11.setToY(-50);
        transition11.setAutoReverse(true);
        transition11.setCycleCount(TranslateTransition.INDEFINITE);
        transition11.play();

        // Circle 12 - Center right side
        TranslateTransition transition12 = new TranslateTransition(Duration.seconds(4), circle12);
        transition12.setFromX(950);
        transition12.setFromY(0);
        transition12.setToX(1050);
        transition12.setToY(75);
        transition12.setAutoReverse(true);
        transition12.setCycleCount(TranslateTransition.INDEFINITE);
        transition12.play();
    }


}
