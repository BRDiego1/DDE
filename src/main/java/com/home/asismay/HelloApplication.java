package com.home.asismay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Marina");
        stage.setScene(scene);
        Image icon = new Image(getClass().getResourceAsStream("/image/img.png"));
        stage.getIcons().add(icon);
        stage.sizeToScene();
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
