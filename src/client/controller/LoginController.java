package client.controller;

import client.StartClient;
import client.logic.Client;
import client.screen.AppScreen;
import client.utils.Run;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import network.entity.LoginResponse;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;
    
    @FXML
    private Button login;
    
    @FXML
    private Button cancel;
    
    private BooleanProperty disable;
    
    @FXML
    public void initialize() {
        disable = new SimpleBooleanProperty();
        disable.bindBidirectional(username.disableProperty());
        disable.bindBidirectional(password.disableProperty());
        disable.bindBidirectional(cancel.disableProperty());
        disable.bindBidirectional(login.disableProperty());
    }

    @FXML
    private void login() {
        disable.setValue(true);
        Run.safe(() ->
                StartClient.getSocketManager().login(
                        username.getText(),
                        password.getText(),
                        LoginController.class,
                        this::onLogin
                ), 
                this::onError
        );
    }

    private void onLogin(final LoginResponse response) {
        disable.setValue(false);
        Client.getAppLogic().processLoginResponse(response);
    }

    private void onError() {
        Platform.runLater(() -> disable.setValue(false));
    }
    

    public void cancel() {
        AppScreen.back();
    }
}
