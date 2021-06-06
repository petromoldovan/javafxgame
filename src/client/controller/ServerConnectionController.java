package client.controller;

import client.StartClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import server.StartServer;

public class ServerConnectionController {
    public TextField hostField;
    public TextField portField;

//    public ServerConnectionController() {
//        this.hostField.setV("127.0.0.1");
//        this.portField.setText("5656");
//    }

    @FXML
    private void handleButtonAction (ActionEvent event) throws Exception {
        String host = "";
        int port = 0;

        // validate entries
        try {
            host = hostField.getText();
            port = Integer.parseInt(portField.getText());
            if (host.equalsIgnoreCase("")) {
                throw new Exception("invalid host");
            }

            if (port < 1 || port > 655535) {
                throw new Exception("invalid port");
            }
        } catch (Exception e) {
            System.out.println("ERROR: ServerConnectionController#handleButtonAction" + e.getMessage());
        }

        System.out.println("port" + port);
        System.out.println("host " + host);

        // try to connect to server
        connectToServer(host, port);
    }

    private void connectToServer(String host, int port) {
        new Thread(() -> {
           System.out.println("trying to connect...");

           if (StartClient.socketManager.connect(host, port)) {
               System.out.println("SUCCESS");
               next();
           } else {
               System.out.println("FAILURE");
           }
        })
        .start();
    }

    private void next() {
        ScreenController screenController = ScreenController.getInstance();
        screenController.activate("registrationScreen");
    }
}
