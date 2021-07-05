package client.controller;

import client.StartClient;
import client.screen.AppScreen;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {
    
    @FXML
    public Button singleplayerBtn;
    
    @FXML
    public Button multiplayerBtn;

    public void showProfile(ActionEvent actionEvent) {
    }

    public void startSinglegame(ActionEvent actionEvent) {
        StartClient.getSocketManager().startSingleMatch();
    }

    public void startMultiplayer(ActionEvent actionEvent) {
        singleplayerBtn.setDisable(true);
        multiplayerBtn.setText("waiting for opponent");
        multiplayerBtn.setDisable(true);
        StartClient.getSocketManager().findMatch();
    }
    
    @FXML
    public void showScores() {
        AppScreen.SCORES.goFrom(DashboardController.class);
    }
    
    @FXML
    public void exit() {
        Platform.exit();
    }
}
