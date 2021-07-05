package client.controller;

import client.StartClient;
import client.screen.AppScreen;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {
    
    @FXML
    public Button singleplayerBtn;
    
    @FXML
    public Button multiplayerBtn;
    
    @FXML
    public Button scores;

    private BooleanProperty disable;
    
    @FXML
    public void initialize() {
        disable = new SimpleBooleanProperty(false);
        disable.bindBidirectional(singleplayerBtn.disableProperty());
        disable.bindBidirectional(multiplayerBtn.disableProperty());
        disable.bindBidirectional(scores.disableProperty());
    }

    public void startSinglegame(ActionEvent actionEvent) {
        StartClient.getSocketManager().startSingleMatch();
    }

    public void startMultiplayer(ActionEvent actionEvent) {
        multiplayerBtn.setText("waiting for opponent");
        disable.setValue(true);
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
