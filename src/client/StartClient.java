package client;

import client.controller.ScreenController;
import client.controller.SocketManager;
import client.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartClient extends Application {
    public static Stage window;
    public static Scene rootScreen;

    public static int WIDTH = 800;
    public static int HEIGHT = 600;

    public static SocketManager socketManager;

    public static void main(String[] args) {
        launch(args);
    }


    //
    public static GameController gameScreenController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        socketManager = new SocketManager();

        window = primaryStage;
        window.setTitle("FROGGER GAME");

        //TODO:
        gameScreenController = new GameController();

        Parent rootRegistration = FXMLLoader.load(getClass().getResource("/client/resources/home.fxml"));
        rootScreen = new Scene(rootRegistration, WIDTH, HEIGHT);

        ScreenController screenController = new ScreenController(rootScreen);
        screenController.add("serverConnectionScreen", FXMLLoader.load(getClass().getResource("/client/resources/serverConnection.fxml")));
        screenController.add("registrationScreen", FXMLLoader.load(getClass().getResource("/client/resources/home.fxml")));
        screenController.add("dashboardScreen", FXMLLoader.load(getClass().getResource("/client/resources/dashboard.fxml")));
        screenController.add("playgroundScreen", FXMLLoader.load(getClass().getResource("/client/resources/playground.fxml")));
        screenController.add("boardScreen", FXMLLoader.load(getClass().getResource("/client/resources/board.fxml")));
        screenController.activate("serverConnectionScreen");

        // render registration scene
        window.setScene(rootScreen);

        window.show();
    }
}
