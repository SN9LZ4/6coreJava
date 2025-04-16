package tn.chouflifilm.controller.Ressource;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import tn.chouflifilm.entities.User;
import tn.chouflifilm.entities.ressource;
import tn.chouflifilm.services.Association.associationService;
import tn.chouflifilm.services.Reclamation.serviceReclamation;
import tn.chouflifilm.services.Ressources.ressourceService;
import tn.chouflifilm.tools.UserSessionManager;

import java.sql.SQLException;

public class addRessourceFront {
    private tn.chouflifilm.services.Ressources.ressourceService ressourceService = new ressourceService();
    @FXML
    private ComboBox<String> typeComboBox;
    public void addRessource() throws SQLException {
        String  besoin_specifique = typeComboBox.getValue();
        User user = UserSessionManager.getInstance().getCurrentUser();
        ressource  ressource = new ressource(UserSessionManager.getInstance().getAssociation().getId(),UserSessionManager.getInstance().getCurrentUser().getId(),besoin_specifique);
ressourceService.ajouterRessource(ressource);

    }
}
