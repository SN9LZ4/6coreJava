package tn.chouflifilm.controller.user;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.chouflifilm.entities.User;
import tn.chouflifilm.services.userService;
import tn.chouflifilm.tools.UserSessionManager;

import java.io.IOException;
import java.sql.SQLException;

public class frontController {
    private tn.chouflifilm.services.userService userService = new userService();

    @FXML
    private ImageView imageView;
@FXML
private TextField nomTextField;

    @FXML
    public void initialize() throws SQLException {
User user= userService.recherparid(UserSessionManager.getInstance().getCurrentUser().getId());
        String imageFromDb = user.getimage();


        Image image1 = new Image(imageFromDb);
        imageView.setImage(image1);
    }

    public void pageupdateFront() throws IOException {

        Stage currentStage = (Stage) imageView.getScene().getWindow();
        navbarController.changeScene("/user/updateFont.fxml", currentStage);
    }
    public void pageReclamation() throws IOException {

        Stage currentStage = (Stage) imageView.getScene().getWindow();
        navbarController.changeScene("/Reclamation/affichageReclamationFront.fxml", currentStage);
    }

    public void pageassociation() throws IOException {

        Stage currentStage = (Stage) imageView.getScene().getWindow();
        navbarController.changeScene("/Benevolat/Association/affichageFront.fxml", currentStage);
    }



}
