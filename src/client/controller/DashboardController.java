package client.controller;

import client.StartClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class DashboardController {


    public void showProfile(ActionEvent actionEvent) {
    }

    public void startSinglegame(ActionEvent actionEvent) throws Exception {
//        ScreenController screenController = ScreenController.getInstance();
//        screenController.activate("playgroundScreen");
//        PlaygroundController.startGame();
        StartClient.gameScreenController.show();
        StartClient.gameScreenController.setTimeLeft(60);
        StartClient.gameScreenController.startGame();
        StartClient.gameScreenController.setTimeLeft(59);
    }

    public void startMultiplayer(ActionEvent actionEvent) {
        StartClient.socketManager.findMatch();
    }
}
