package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import tn.chouflifilm.services.Main;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/Reclamation/affichageReclamationFront.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1550, 800);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        stage.setTitle("LoginPage");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
