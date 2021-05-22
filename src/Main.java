import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import controllers.ScreenController;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.util.Duration;


public class Main extends Application {
    Stage window;
    Scene rootScreen;

    int WIDTH = 800;
    int HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("AWESOME GAME");

        Parent rootRegistration = FXMLLoader.load(getClass().getResource("resources/home.fxml"));
        rootScreen = new Scene(rootRegistration, WIDTH, HEIGHT);

        ScreenController screenController = new ScreenController(rootScreen);
        screenController.add("home", FXMLLoader.load(getClass().getResource( "./resources/home.fxml" )));
        screenController.add("playground", FXMLLoader.load(getClass().getResource( "./resources/playground.fxml" )));
        screenController.add("board", FXMLLoader.load(getClass().getResource( "./resources/board.fxml" )));
        screenController.activate("home");

        // render registration scene
        window.setScene(rootScreen);

        window.show();
    }
}
