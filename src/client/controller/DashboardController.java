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
        ScreenController screenController = ScreenController.getInstance();
        screenController.activate("playgroundScreen");
        PlaygroundController.startGame();
    }

    public void startMultiplayer(ActionEvent actionEvent) {
        StartClient.socketManager.findMatch();
    }
}
