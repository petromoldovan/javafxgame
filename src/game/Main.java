package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    Stage window;
    Scene sceneRegistration, scenePlayground, sceneBoard;

    int WIDTH = 400;
    int HEIGHT = 400;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("AWESOME GAME");

        // 1. Registration - scene where user enters username
        Label titleRegistration = new Label("Enter your name");
        Button button1 = new Button("next");
        button1.setOnAction(e -> window.setScene(scenePlayground));
        VBox layoutRegistration = new VBox(20);
        layoutRegistration.getChildren().addAll(titleRegistration, button1);
        sceneRegistration = new Scene(layoutRegistration, WIDTH, HEIGHT);

        // 2. Game - scene where game is rendered
        Button button2 = new Button("Continue");
        button2.setOnAction(e -> window.setScene(sceneBoard));
        VBox gameLayout = new VBox(20);
        Label titleGameScene = new Label("Game scene");
        gameLayout.getChildren().addAll(titleGameScene, button2);
        scenePlayground = new Scene(gameLayout, WIDTH, HEIGHT);

        // 3. High Score Board - scene where user scores are displayed
        VBox boardLayout = new VBox(20);
        Label titleBoard = new Label("Game scene");
        boardLayout.getChildren().addAll(titleBoard);
        sceneBoard = new Scene(boardLayout, WIDTH, HEIGHT);

        // render initial scene
        window.setScene(sceneRegistration);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
