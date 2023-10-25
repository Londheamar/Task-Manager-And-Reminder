package application;

import controllers.ViewController;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewController viewController = new ViewController(primaryStage);
        viewController.loadView("/views/login.fxml");   
    }

    public static void main(String[] args) {
        launch(args);
    }
}