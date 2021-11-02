package com.dnn.clientapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("welcomeWindow.fxml"));
        stage.setTitle("Welcome");
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        WelcomeWindowController windowController = fxmlLoader.getController();
        stage.setOnCloseRequest(windowController.getCloseEventHandler());
        windowController.firstEntry();
    }


    public static void main(String[] args) {
        launch(args);
    }

}