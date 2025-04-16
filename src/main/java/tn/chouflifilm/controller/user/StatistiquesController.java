package tn.chouflifilm.controller.user;
import com.example.demo.HelloApplication;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.chouflifilm.tools.UserSessionManager;

import java.io.IOException;

public class StatistiquesController {

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
    private BarChart<String, Number> home_totalEnrolledChart;

    @FXML
    private AreaChart<String, Number> home_totalFemaleChart;

    @FXML
    private LineChart<String, Number> home_totalMaleChart;

    @FXML
    public void initialize() {
        loadTotalEnrolledChart();
        loadTotalFemaleChart();
        loadTotalMaleChart();
        animateBackgroundCircles();
    }

    private void loadTotalEnrolledChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Users");
        series.getData().add(new XYChart.Data<>("Jan", 150));
        series.getData().add(new XYChart.Data<>("Feb", 180));
        series.getData().add(new XYChart.Data<>("Mar", 220));
        series.getData().add(new XYChart.Data<>("Apr", 140));
        series.getData().add(new XYChart.Data<>("May", 200));

        home_totalEnrolledChart.getData().clear();
        home_totalEnrolledChart.getData().add(series);
        Platform.runLater(() -> {
            // Modifier la couleur de la ligne
            if (series.getNode() != null) {
                series.getNode().setStyle("-fx-stroke: rgba(255,51,51,0.2); -fx-stroke-width: 2px;");
            }

            // Modifier la couleur des points
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-background-color: rgba(255,51,51,0.2);");
                }
            }
        });
    }

    private void loadTotalFemaleChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Femmes Inscrites");
        series.getData().add(new XYChart.Data<>("Jan", 80));
        series.getData().add(new XYChart.Data<>("Feb", 90));
        series.getData().add(new XYChart.Data<>("Mar", 100));
        series.getData().add(new XYChart.Data<>("Apr", 70));
        series.getData().add(new XYChart.Data<>("May", 120));

        home_totalFemaleChart.getData().clear();
        home_totalFemaleChart.getData().add(series);


    }

    private void loadTotalMaleChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Hommes Inscrits");
        series.getData().add(new XYChart.Data<>("Jan", 70));
        series.getData().add(new XYChart.Data<>("Feb", 90));
        series.getData().add(new XYChart.Data<>("Mar", 120));
        series.getData().add(new XYChart.Data<>("Apr", 100));
        series.getData().add(new XYChart.Data<>("May", 80));

        home_totalMaleChart.getData().clear();
        home_totalMaleChart.getData().add(series);
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
    public void logout() throws IOException {
        UserSessionManager.getInstance().logout();
        String file = "hello-view.fxml";

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(file));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 800);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();


        Stage currentStage = (Stage) circle1.getScene().getWindow();
        currentStage.close();

    }
    public void pageuser() throws IOException {
        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("backback.fxml", currentStage);
    }
    public void pageAssociation() throws IOException {
        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("/Benevolat/Association/affichageBack.fxml", currentStage);
    }
    public void pageReclamation() throws IOException {

        Stage currentStage = (Stage) circle1.getScene().getWindow();
        navbarController.changeScene("/Reclamation/reclamationBack.fxml", currentStage);
    }


}
