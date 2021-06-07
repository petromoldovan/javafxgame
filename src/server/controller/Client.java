package server.controller;

import common.constants.ActionTypes;
import server.StartServer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (StartServer.isServerRunning) {
            try {
                // read request from the client
                String messageFromClient = dataInputStream.readUTF();
                System.out.println("GOT DATA!!!" + messageFromClient);
                ActionTypes.ActionType type = ActionTypes.getActionTypeFromMessage(messageFromClient);

                System.out.println("GOT messsage type!!!" + type);

                switch (type) {
                    case LOGIN_USER:
                        onLoginUser(messageFromClient);
                        break;
                    case INVALID:
                        System.out.println("ERROR: invalid type");
                        break;
                    default:
                        System.out.println("ERROR: unknown type");
                }

            } catch (IOException e) {
                System.out.println("ERROR: Client#run" + e.getMessage());
                break;
            }
        }
    }

    public String sendDataToClient(String data) {
        try {
            // TODO: encrypt data?
            this.dataOutputStream.writeUTF(data);
            return "SUCCESS";
        } catch (IOException e) {
            System.out.println("ERROR: Client#sendData" + e.getMessage());
            return "FAILURE";
        }
    }

    private void onLoginUser(String message) {
        String[] splitted = message.split(";");
        String email = splitted[1];

        // TODO: login check

        // send user data to
        sendDataToClient(ActionTypes.ActionType.LOGIN_USER.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + email);
    }
}
