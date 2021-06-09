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
    String clientID = null;

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
            // set client id
            this.clientID = splitted[2];

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

            System.out.println("message " + message);

            Platform.runLater(
                    () -> {
                        try {
                            // initialize the game
                            StartClient.gameScreenController.show();

                            // TODO: set players
                            Player p1 = new Player(splitted[3], splitted[4]);
                            StartClient.gameScreenController.setPlayer1(p1);
                            String p2ID = splitted[5];
                            if (!p2ID.equals("")) {
                                System.out.println("setting second player...");
                                Player p2 = new Player(splitted[5], splitted[6]);
                                StartClient.gameScreenController.setPlayer2(p2);
                            }
                            // set the frog that is controlled by the current client
                            if (p1.getID().equals(this.clientID)) {
                                System.out.println("I AM THE FIRST CLIUENT!");
                                StartClient.gameScreenController.isControllingFirstFrog(true);
                            } else {
                                System.out.println("I AM THE FIRST SECOND CLEINT!");
                                // user is controlling the second frog
                                StartClient.gameScreenController.isControllingFirstFrog(false);
                            }

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

        String player2ID = splitted[4];

        Platform.runLater(
                () -> {
                    try {
                        StartClient.gameScreenController.setTimeLeft(timeLeft);
                        // set player position
                        StartClient.gameScreenController.setX1(splitted[7]);
                        StartClient.gameScreenController.setY1(splitted[8]);

                        // add second player
                        if (!player2ID.equals("")) {
                            StartClient.gameScreenController.setX2(splitted[9]);
                            StartClient.gameScreenController.setY2(splitted[10]);
                        }

                    } catch (Exception e) {
                        System.out.println("onGetDataForRoomResponse# " + e.getMessage());
                    }
                }
        );

        return message;
    }
}
