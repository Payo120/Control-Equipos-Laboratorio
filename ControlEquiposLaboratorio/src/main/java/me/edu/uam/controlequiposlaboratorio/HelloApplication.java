package me.edu.uam.controlequiposlaboratorio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.edu.uam.controlequiposlaboratorio.persistence.JpaUtil;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 950, 620);
        stage.setTitle("Control de Equipos de Laboratorio - JavaFX & Hibernate");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        JpaUtil.cerrar();
    }

    public static void main(String[] args) {
        launch();
    }
}