package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewController {

    private Stage stage;

    public ViewController(@SuppressWarnings("exports") Stage stage) {
        this.stage = stage;
    }

    public void loadView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800, Color.AQUA);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            stage.setScene(scene);
            
            Image icon = new Image("file:/assets/logo.png");
            
            stage.getIcons().add(icon);
            
            stage.setTitle("Task Manager and Reminder");
            
            stage.setResizable(false);
            
            stage.show();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
