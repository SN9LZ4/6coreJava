package Controller;

import com.itextpdf.text.DocumentException;
import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import tn.esprit.entities.article;

import java.io.IOException;

public class ArticleStatsController {
    @FXML private Label avgRatingLabel;
    @FXML private Label ratingCountLabel;
    @FXML private Label likesLabel;
    @FXML private Label dislikesLabel;
    @FXML private HBox starsContainer;
    @FXML private BarChart<String, Number> ratingDistributionChart;
    @FXML private PieChart reactionsPieChart;
    @FXML private Button exportPdfButton;
    
    private article currentArticle;

    private void animateValue(Label label, double start, double end, int duration) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(new SimpleDoubleProperty(start), start)),
            new KeyFrame(Duration.millis(duration), event -> {
                if (end == (int) end) {
                    label.setText(String.format("%d", (int)end));
                } else {
                    label.setText(String.format("%.1f", end));
                }
            }, new KeyValue(new SimpleDoubleProperty(end), end, Interpolator.SPLINE(0.4, 0, 0.2, 1)))
        );
        timeline.play();
    }

    public void setArticle(article a) {
        this.currentArticle = a;
        
        // Configuration initiale du PieChart
        reactionsPieChart.setStartAngle(90);
        reactionsPieChart.setLabelsVisible(false);
        reactionsPieChart.setAnimated(false); // On va gérer nos propres animations

        // Mettre à jour le graphique en camembert
        reactionsPieChart.getData().clear();
        PieChart.Data likeData = new PieChart.Data("J'aime", 0);
        PieChart.Data dislikeData = new PieChart.Data("Je n'aime pas", 0);
        reactionsPieChart.getData().addAll(likeData, dislikeData);

        // Animation moderne des valeurs
        Timeline valueAnimation = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(likeData.pieValueProperty(), 0),
                new KeyValue(dislikeData.pieValueProperty(), 0)
            ),
            new KeyFrame(Duration.millis(800),
                new KeyValue(likeData.pieValueProperty(), a.getLikes(), Interpolator.SPLINE(0.4, 0, 0.2, 1)),
                new KeyValue(dislikeData.pieValueProperty(), a.getDislikes(), Interpolator.SPLINE(0.4, 0, 0.2, 1))
            )
        );

        // Animation de rotation moderne
        reactionsPieChart.setRotate(0);
        Timeline rotateAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(reactionsPieChart.rotateProperty(), -90)),
            new KeyFrame(Duration.millis(1000), 
                new KeyValue(reactionsPieChart.rotateProperty(), 0, Interpolator.SPLINE(0.4, 0, 0.2, 1))
            )
        );

        // Animation d'opacité
        reactionsPieChart.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), reactionsPieChart);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);

        // Jouer toutes les animations
        ParallelTransition parallelTransition = new ParallelTransition(
            valueAnimation,
            rotateAnimation,
            fadeIn
        );
        parallelTransition.play();

        // Animations au survol modernes
        for (PieChart.Data data : reactionsPieChart.getData()) {
            Node slice = data.getNode();
            
            // Configuration initiale
            slice.setScaleX(1);
            slice.setScaleY(1);
            
            // Créer un tooltip
            Tooltip tooltip = new Tooltip(
                String.format("%s\n%d", data.getName(), (int)data.getPieValue())
            );
            tooltip.setStyle(
                "-fx-background-color: -fx-blue-900;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 8px 12px;" +
                "-fx-background-radius: 6px;"
            );
            
            // Créer un effet de survol moderne
            slice.setOnMouseEntered(e -> {
                // Scale animation
                Timeline scaleAnimation = new Timeline(
                    new KeyFrame(Duration.millis(200),
                        new KeyValue(slice.scaleXProperty(), 1.05, Interpolator.SPLINE(0.4, 0, 0.2, 1)),
                        new KeyValue(slice.scaleYProperty(), 1.05, Interpolator.SPLINE(0.4, 0, 0.2, 1))
                    )
                );
                scaleAnimation.play();
                
                // Afficher le tooltip
                Tooltip.install(slice, tooltip);
            });

            slice.setOnMouseExited(e -> {
                Timeline scaleAnimation = new Timeline(
                    new KeyFrame(Duration.millis(200),
                        new KeyValue(slice.scaleXProperty(), 1, Interpolator.SPLINE(0.4, 0, 0.2, 1)),
                        new KeyValue(slice.scaleYProperty(), 1, Interpolator.SPLINE(0.4, 0, 0.2, 1))
                    )
                );
                scaleAnimation.play();
                
                // Cacher le tooltip
                Tooltip.uninstall(slice, tooltip);
            });
        }

        // Animation des labels de statistiques
        animateValue(avgRatingLabel, 0, a.getTotal_rating(), 800);
        animateValue(likesLabel, 0, a.getLikes(), 800);
        animateValue(dislikesLabel, 0, a.getDislikes(), 800);
        
        // Afficher le nombre de votes avec animation
        int voteCount = a.getRating_count();
        animateValue(ratingCountLabel, 0, voteCount, 1000);

        // Animation des étoiles
        starsContainer.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView();
            star.setFitWidth(20);
            star.setFitHeight(20);
            star.setOpacity(0); // Commence invisible

            if (i < Math.round(a.getTotal_rating())) {
                star.setImage(new Image(getClass().getResourceAsStream("/images/star.png")));
            } else {
                star.setImage(new Image(getClass().getResourceAsStream("/images/favorite.png")));
            }
            
            // Animation d'entrée pour chaque étoile
            FadeTransition ft = new FadeTransition(Duration.millis(200), star);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(i * 100));
            ft.play();
            
            // Animation de scale au survol
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(100), star);
            scaleIn.setToX(1.2);
            scaleIn.setToY(1.2);
            
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(100), star);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            
            star.setOnMouseEntered(e -> scaleIn.playFromStart());
            star.setOnMouseExited(e -> scaleOut.playFromStart());
            
            starsContainer.getChildren().add(star);
        }

        // Mettre à jour le graphique de distribution avec animation
        ratingDistributionChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Distribution des notes");

        // Ajouter les données avec animation
        for (int i = 1; i <= 5; i++) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(String.valueOf(i), 0);
            series.getData().add(data);
            
            Timeline barAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(data.YValueProperty(), 0)
                ),
                new KeyFrame(Duration.millis(1000), 
                    new KeyValue(data.YValueProperty(), getRatingCount(a, i), Interpolator.EASE_OUT)
                )
            );
            barAnimation.setDelay(Duration.millis(i * 100));
            barAnimation.play();
        }
        
        ratingDistributionChart.getData().add(series);
    }

    // Méthode utilitaire pour obtenir le nombre de votes pour chaque note
    private int getRatingCount(article a, int rating) {
        // À implémenter selon votre logique de stockage des notes
        // Pour l'exemple, retourne une valeur aléatoire
        return (int) (Math.random() * 10);
    }
    
    @FXML
    private void exportToPdf() {
        try {
            PDFGenerator.generateArticleStatsPDF(currentArticle, reactionsPieChart, ratingDistributionChart);
            showAlert(Alert.AlertType.INFORMATION, "PDF généré avec succès", 
                     "Le PDF a été enregistré dans votre dossier Téléchargements.");
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de générer le PDF: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
