package client.controller;

import client.StartClient;
import client.model.Player;
import com.sun.xml.internal.xsom.impl.scd.Step;
import common.constants.ActionTypes;
import javafx.application.Platform;

import javax.imageio.IIOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketManager {
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Thread listener = null;
    String roomID = null;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 2000);

            System.out.println("Connected to server " + host + ":" + port);

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            listener = new Thread(this::readDataFromServer);
            listener.start();
            return true;
        } catch (IOException e) {
            System.out.println("ERROR: SocketHandler#connect " + e.getMessage());
            return false;
        }
    }

    public void readDataFromServer() {
        boolean isRunning = true;

        while(isRunning) {
            try {
                String messageFromServer = dataInputStream.readUTF();

                // parse type
                ActionTypes.ActionType type = ActionTypes.getActionTypeFromMessage(messageFromServer);

                switch (type) {
                    case LOGIN_USER:
                        onLoginUserResponse(messageFromServer);
                        break;
                    case FIND_MATCH:
                        onFindMatchResponse(messageFromServer);
                        break;
                    case JOIN_ROOM:
                        onJoinRoomResponse(messageFromServer);
                        break;
//                    case GET_DATA_FOR_ROOM_RESPONSE:
//                        onGetDataForRoomResponse(messageFromServer);
//                        break;
                    case CURRENT_GAME_DATA_RESPONSE:
                        onCurrentGameDataResponse(messageFromServer);
                        break;
                    case INVALID:
                        System.out.println("ERROR: invalid type " + type);
                        break;
                    default:
                        System.out.println("ERROR: unknown type " + type);
                }
            } catch (IOException e) {
                System.out.println("ERROR: SocketHandler#readDataFromServer " + e.getMessage());
            }
        }
    }

    private void sendDataToServer(String data) {
        try {
            dataOutputStream.writeUTF(data);
        } catch (IOException e) {
            System.out.println("ERROR: SocketHandler#sendDataToServer " + e.getMessage());
        }
    }

    // Requests to server

    public void login(String username, String password) {
        String payload = ActionTypes.ActionType.LOGIN_USER + ";" + username + ";" + password;
        sendDataToServer(payload);
    }

    public void findMatch() {
        String payload = ActionTypes.ActionType.FIND_MATCH + ";";
        sendDataToServer(payload);
    }

//    public void getDataForTheRoomRequest(String roomID) {
//        sendDataToServer(ActionTypes.ActionType.GET_DATA_FOR_ROOM_REQUEST.name() + ";" + roomID);
//    }

    public void updateGamePosition(int x, int y) {
        sendDataToServer(ActionTypes.ActionType.UPDATE_GAME_POSITION_REQUEST.name() + ";" + this.roomID + ";" + x + ";" + y);
    }

    public void startSingleMatch() {
        String payload = ActionTypes.ActionType.START_SINGLE_MATCH_REQUEST.name() + ";";
        sendDataToServer(payload);
    }

    // Responses from server

    private void onLoginUserResponse(String message) {
        String[] splitted = message.split(";");
        String status = splitted[1];

        if (status.equalsIgnoreCase(ActionTypes.Code.SUCCESS.name())) {
            ScreenController screenController = ScreenController.getInstance();
            screenController.activate("dashboardScreen");
        } else {
            System.out.println("ERROR: onLoginUserResponse# login fail");
        }
    }

    private void onFindMatchResponse(String message) {
        String[] splitted = message.split(";");
        String status = splitted[1];

        if (status.equalsIgnoreCase(ActionTypes.Code.SUCCESS.name())) {
            // TODO: show message that you are queued up for the match
            System.out.println("waiting...");
        } else {
            System.out.println("ERROR: onFindMatchResponse# smth is wrong");
        }
    }

    private void onJoinRoomResponse(String message) {
        String[] splitted = message.split(";");
        String status = splitted[1];

        //System.out.println("join room " + message);

        if (status.equalsIgnoreCase(ActionTypes.Code.SUCCESS.name())) {
            this.roomID = splitted[2];

            // navigate to playground
//            ScreenController screenController = ScreenController.getInstance();
//            screenController.activate("playgroundScreen");
//
//            // retrieve data for the room
//            getDataForTheRoomRequest(this.roomID);
//
//            try {
//                PlaygroundController.startGame();
//            } catch (Exception e) {
//                System.out.println("ERROR: onGetMultiplayerMatchInfoResponse#" + e.getMessage());
//            }
            Platform.runLater(
                    () -> {
                        try {
                            // initialize the game
                            StartClient.gameScreenController.show();
                            //StartClient.gameScreenController.setTimeLeft(60);

                            // TODO: set players
                            // TODO: set room id

                            StartClient.gameScreenController.startGame();
                        } catch (Exception e) {
                            System.out.println("onGetDataForRoomResponse# " + e.getMessage());
                        }
                    }
            );

        } else {
            System.out.println("ERROR: onGetMultiplayerMatchInfoResponse# smth is wrong");
        }
    }

//    private String onGetDataForRoomResponse(String message) {
//        String[] splitted = message.split(";");
//        int timeLeft = Integer.parseInt(splitted[5]);
//
//        // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
//        Platform.runLater(
//                () -> {
//                    try {
//                        // initialize the game
//                        StartClient.gameScreenController.show();
//                        StartClient.gameScreenController.setTimeLeft(timeLeft);
//
//                        // TODO: set players
//                        // TODO: set room id
//
//                        StartClient.gameScreenController.startGame();
//                    } catch (Exception e) {
//                        System.out.println("onGetDataForRoomResponse# " + e.getMessage());
//                    }
//                }
//        );
//
//        return message;
//    }

    private String onCurrentGameDataResponse(String message) {
        String[] splitted = message.split(";");
        int timeLeft = Integer.parseInt(splitted[6]);

        System.out.println("got data " + message);

        Platform.runLater(
                () -> {
                    try {
                        StartClient.gameScreenController.setTimeLeft(timeLeft);
                        // set player position
                        StartClient.gameScreenController.setX1(splitted[7]);
                        StartClient.gameScreenController.setY1(splitted[8]);
                    } catch (Exception e) {
                        System.out.println("onGetDataForRoomResponse# " + e.getMessage());
                    }
                }
        );

        return message;
    }
}
