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

    private boolean isLookingForMatch = false;
    private String clientID = "";

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
                    case FIND_MATCH:
                        onFindMatchRequest();
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

        // save user id
        this.clientID = email;

        // send user data to
        sendDataToClient(ActionTypes.ActionType.LOGIN_USER.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + email);
    }

    private void onFindMatchRequest() {
        Client opponent = StartServer.clientManager.findOpponent();

        if (opponent == null) {
            // case: nobody is looking for a game.
            // Present yourself as the one that is looking for the game and wait.
            this.isLookingForMatch = true;
            sendDataToClient(ActionTypes.ActionType.FIND_MATCH.name() + ";" + ActionTypes.Code.SUCCESS.name());
        } else {
            // case: opponent found

            // both are not looking for match any longer
            opponent.isLookingForMatch = false;
            this.isLookingForMatch = false;

            // communicate match data to the participating client
//            this.sendDataToClient(ActionTypes.ActionType.GET_MULTIPLAYER_MATCH_INFO.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + opponent.getID());
//            opponent.sendDataToClient(ActionTypes.ActionType.GET_MULTIPLAYER_MATCH_INFO.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + this.getID());

            Room room = StartServer.roomManager.newRoom();
            // add clients to the new room
            room.addClient(this);
            room.addClient(opponent);

            // send confirmation that clients joined the room with id
            this.sendDataToClient(ActionTypes.ActionType.JOIN_ROOM.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + room.getID());
            opponent.sendDataToClient(ActionTypes.ActionType.JOIN_ROOM.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + room.getID());
        }
    }

    public boolean isLookingForMatch() {
        return this.isLookingForMatch;
    }

    public String getID() {
        return this.clientID;
    }
}
