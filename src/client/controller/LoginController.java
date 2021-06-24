package client.controller;

import client.StartClient;
import client.screen.AppScreen;
import client.utils.Run;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private void login() {
        Run.safe(() -> StartClient.getSocketManager().login(username.getText(), password.getText()));
    }
    
    public void cancel() {
        AppScreen.back();
    }
}
