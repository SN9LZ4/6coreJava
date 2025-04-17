// ListeCommentaires.java
package Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import tn.esprit.entities.article;
import tn.esprit.entities.commentaire;
import tn.esprit.services.ServiceException;
import tn.esprit.services.commentaireService;

import java.util.List;

public class ListeCommentaires {

    @FXML
    private TableView<commentaire> commentaireTable;

    @FXML
    private TableColumn<commentaire, String> contenuColumn;

    @FXML
    private TableColumn<commentaire, String> nomColumn;

    @FXML
    private TableColumn<commentaire, Void> actionColumn;

    private final commentaireService commentaireService = new commentaireService();

    private article currentArticle;

    public void setArticle(article a) {
        this.currentArticle = a;
        afficherCommentaires();
    }

    public void afficherCommentaires() {
        try {
            List<commentaire> commentaires = commentaireService.getCommentairesByArticle(currentArticle);
            ObservableList<commentaire> observableList = FXCollections.observableArrayList(commentaires);
            commentaireTable.setItems(observableList);
        } catch (ServiceException e) {
            showAlert("Erreur de chargement des commentaires : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenuCom"));
        nomColumn.setCellValueFactory(cellData -> {
            String nom = cellData.getValue().getUser().getNom();
            String prenom = cellData.getValue().getUser().getPrenom();
            return new ReadOnlyStringWrapper(nom + " " + prenom);
        });

        actionColumn.setCellFactory(getActionCellFactory());
    }

    private Callback<TableColumn<commentaire, Void>, TableCell<commentaire, Void>> getActionCellFactory() {
        return column -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");

            {
                deleteBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");
                deleteBtn.setOnAction(event -> {
                    commentaire selected = getTableView().getItems().get(getIndex());
                    try {
                        commentaireService.delete(selected, selected.getUser()); // vous pouvez adapter l'utilisateur ici
                        getTableView().getItems().remove(selected);
                    } catch (ServiceException e) {
                        showAlert("Erreur lors de la suppression.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        };
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
