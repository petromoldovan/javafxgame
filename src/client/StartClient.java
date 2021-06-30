package client;

import client.controller.GameController;
import client.game.SocketManager;
import client.screen.AppScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class StartClient extends Application {

    private static SocketManager socketManager;
    private static GameController gameScreenController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        socketManager = new SocketManager();
        gameScreenController = new GameController();
        AppScreen.SERVER_CONNECTION.goFrom(null);
    }

    public static SocketManager getSocketManager() {
        return socketManager;
    }

    public static GameController getGameController() {
        return gameScreenController;
    }
}
