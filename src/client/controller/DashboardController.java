package client.controller;

import client.StartClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class DashboardController {
    public Button singleplayerBtn;
    public Button multiplayerBtn;

    public void showProfile(ActionEvent actionEvent) {
    }

    public void startSinglegame(ActionEvent actionEvent) throws Exception {
        StartClient.socketManager.startSingleMatch();
    }

    public void startMultiplayer(ActionEvent actionEvent) {
        singleplayerBtn.setDisable(true);
        multiplayerBtn.setText("waiting for opponent");
        multiplayerBtn.setDisable(true);
        StartClient.socketManager.findMatch();
    }
}
