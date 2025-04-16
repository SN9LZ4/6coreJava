package tn.chouflifilm.controller.reclamation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import tn.chouflifilm.entities.Reclamation;
import tn.chouflifilm.entities.User;
import tn.chouflifilm.services.Reclamation.serviceReclamation;
import tn.chouflifilm.services.userService;

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
    private tn.chouflifilm.services.userService userService = new userService();
    private serviceReclamation serviceReclamation = new serviceReclamation();
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
        String description = descriptionTextArea.getText();
        String type = typeComboBox.getValue();
        String imagePathInput = imagePathLabel.getText();
        Path sourcePath = Paths.get(imagePathInput);

        // Créer le dossier ressources/images s'il n'existe pas
        Path destinationFolder = Paths.get("src/main/resources/images");
        Files.createDirectories(destinationFolder);

        // Générer un nom de fichier unique pour éviter les écrasements
        String fileName = sourcePath.getFileName().toString();
        Path destinationPath = destinationFolder.resolve(fileName);

        // Gérer les fichiers en double
        int counter = 1;
        while (Files.exists(destinationPath)) {
            String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
            destinationPath = destinationFolder.resolve(fileNameWithoutExtension + "_" + counter + fileExtension);
            counter++;
        }

        // Copier le fichier (avec remplacement si nécessaire)
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);



        System.out.println("Image déplacée avec succès vers : " + destinationPath);
        System.out.println(description  + type + imagePathInput);


        User user = userService.recherparid(35);

        Reclamation reclamation = new Reclamation(user,imagePathInput,type,"En cours",description,java.sql.Date.valueOf(java.time.LocalDate.now()),"medium");
serviceReclamation.ajouterReclamation(reclamation);

}
}
