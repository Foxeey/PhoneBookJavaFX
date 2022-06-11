package com.example.phonebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class PhoneBookApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sceneStart.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Phone Book");
        stage.setScene(scene);
        stage.setResizable(false);
        Image logo = new Image("logo2.png");
        stage.getIcons().add(logo);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}