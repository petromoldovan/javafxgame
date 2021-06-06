package client.controller;

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

                // TODO: logic based on type
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

    public void login(String username, String password) {
        String payload = ActionTypes.ActionType.LOGIN_USER + ";" + username + ";" + password;
        sendDataToServer(payload);
    }
}
