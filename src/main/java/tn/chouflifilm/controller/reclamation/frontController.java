package tn.chouflifilm.controller.reclamation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.chouflifilm.controller.user.navbarController;
import tn.chouflifilm.entities.Reclamation;
import tn.chouflifilm.entities.User;
import tn.chouflifilm.services.Reclamation.serviceReclamation;
import tn.chouflifilm.services.userService;
import tn.chouflifilm.tools.UserSessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;

public class frontController {
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Button imageChooserButton;

    @FXML
    private Label imagePathLabel;
    @FXML
    private Label typeErrorLabel;
    @FXML
    private Label descriptionErrorLabel;
    @FXML
    private Label imageErrorLabel;
    @FXML
    private ImageView imageView;
    private tn.chouflifilm.services.userService userService = new userService();
    private serviceReclamation serviceReclamation = new serviceReclamation();
    @FXML
    public void initialize() throws SQLException {

        User user = userService.recherparid(UserSessionManager.getInstance().getCurrentUser().getId());

        String imageFromDb = user.getimage();
        Image image1 = new Image(imageFromDb);
        imageView.setImage(image1);
    }

    @FXML
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une Image de Profil");

        // Filtrer uniquement les fichiers image
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            imagePathLabel.setText(selectedFile.getAbsolutePath());
        }
    }


    public void addReclamation() throws IOException, SQLException {
        int compteur =0;
        String description = descriptionTextArea.getText();
        String type = typeComboBox.getValue();
        String imagePathInput = imagePathLabel.getText();
        if (imagePathInput == null || imagePathInput.isEmpty() || imagePathInput.equals("Aucune image sélectionnée")) {
            showError("Image Invalide",imageErrorLabel);
        }
        else{
            imageErrorLabel.setOpacity(0);
        }
        if (description.length()<=3){
            compteur++;
          showError("Description Invalide",descriptionErrorLabel);
        }
        else{
            descriptionErrorLabel.setOpacity(0);
        }
        if (type ==null || type.equals("Sélectionnez le type de réclamation")){
           compteur++;
           showError("Type Invalide",typeErrorLabel);

        }
        else{
            typeErrorLabel.setOpacity(0);
        }
        Path sourcePath = Paths.get(imagePathInput);

        // Créer le dossier ressources/images s'il n'existe pas
        Path destinationFolder = Paths.get("src/main/resources/images");
        Files.createDirectories(destinationFolder);

        // Générer un nom de fichier unique pour éviter les écrasements
        String fileName = sourcePath.getFileName().toString();
        Path destinationPath = destinationFolder.resolve(fileName);
String imago="";
        // Gérer les fichiers en double
        int counter = 1;
        while (Files.exists(destinationPath)) {
            String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));


            String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
            System.out.println("nom du    image ");
            System.out.println(fileNameWithoutExtension + "_" + counter + fileExtension);
         imago="/images/"+fileNameWithoutExtension + "_" + counter + fileExtension;
            destinationPath = destinationFolder.resolve(fileNameWithoutExtension + "_" + counter + fileExtension);
            counter++;
        }

        // Copier le fichier (avec remplacement si nécessaire)
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);



        System.out.println("Image déplacée avec succès vers : " + destinationPath);
        System.out.println(description  + type + imagePathInput);


        User user = userService.recherparid(35);

        Reclamation reclamation = new Reclamation(user,imago,type,"En cours",description,java.sql.Date.valueOf(java.time.LocalDate.now()),"medium");
serviceReclamation.ajouterReclamation(reclamation);
this.pageReclamation();
}
public void pageReclamation() throws IOException {

        Stage currentStage = (Stage) descriptionTextArea.getScene().getWindow();
        navbarController.changeScene("/Reclamation/affichageReclamationFront.fxml", currentStage);
    }
    private void showError(String message,Label nomlabel) {
        nomlabel.setText(message);
        nomlabel.setOpacity(1);
    }
    public void pageFront() throws IOException {

        Stage currentStage = (Stage) descriptionTextArea.getScene().getWindow();
        navbarController.changeScene("/user/front.fxml", currentStage);

    }
    public void pageupdateFront() throws IOException {

        Stage currentStage = (Stage) imageView.getScene().getWindow();
        navbarController.changeScene("/user/editFront.fxml", currentStage);
    }
    public void logout() throws IOException {
        UserSessionManager.getInstance().logout();
        Stage currentStage = (Stage) imageView.getScene().getWindow();
        navbarController.changeScene("hello-view.fxml", currentStage);
    }
}
