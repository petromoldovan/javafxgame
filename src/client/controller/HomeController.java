package client.controller;

import client.screen.AppScreen;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class HomeController {
    
    @FXML
    public void register() {
        AppScreen.REGISTRATION.goFrom(HomeController.class);
    }

    @FXML
    public void login() {
        AppScreen.LOGIN.goFrom(HomeController.class);
    }

    @FXML
    public void exit() {
        Platform.exit();
    }
}
