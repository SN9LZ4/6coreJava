package tn.esprit.tests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class MainFX extends Application {
    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(
                getClass()
                        .getResource("/FrontFilm.fxml")
        );
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("ChoufliFilm.exe");
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
