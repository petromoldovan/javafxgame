package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;


public class Main extends Application {
    Stage window;
    Scene sceneRegistration, scenePlayground, sceneBoard;

    int WIDTH = 400;
    int HEIGHT = 400;

    String username = "";

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("AWESOME GAME");

        // 1. Registration - scene where user enters username
        Label titleRegistration = new Label("Registration");
        Text descriptionTextRegistration = new Text("Give you hero a name");
        descriptionTextRegistration.setFont(Font.font("Tahoma", FontWeight.LIGHT, FontPosture.REGULAR, 25));
        Button button1 = new Button("next");
        button1.setOnAction(e -> window.setScene(scenePlayground));
        VBox layoutRegistration = new VBox(20);
        layoutRegistration.setPadding(new Insets(10));
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("username");
        layoutRegistration.getChildren().addAll(titleRegistration, descriptionTextRegistration, usernameTextField, button1);
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
