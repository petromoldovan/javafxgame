package client.controller;

import client.StartClient;
import com.sun.xml.internal.xsom.impl.scd.Step;
import common.constants.ActionTypes;

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
                System.out.println("messageFromServer" + messageFromServer);

                // parse type
                ActionTypes.ActionType type = ActionTypes.getActionTypeFromMessage(messageFromServer);

                switch (type) {
                    case LOGIN_USER:
                        onLoginUserResponse(messageFromServer);
                        break;
                    case FIND_MATCH:
                        onFindMatchResponse(messageFromServer);
                        break;
                    case GET_MULTIPLAYER_MATCH_INFO:
                        onGetMultiplayerMatchInfoResponse(messageFromServer);
                        break;
                    case INVALID:
                        System.out.println("ERROR: invalid type");
                        break;
                    default:
                        System.out.println("ERROR: unknown type");
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

    private void onGetMultiplayerMatchInfoResponse(String message) {
        String[] splitted = message.split(";");
        String status = splitted[1];

        if (status.equalsIgnoreCase(ActionTypes.Code.SUCCESS.name())) {
            System.out.println("got opponent! " + splitted[2]);



        } else {
            System.out.println("ERROR: onGetMultiplayerMatchInfoResponse# smth is wrong");
        }
    }
}
