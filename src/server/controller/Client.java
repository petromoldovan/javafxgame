package server.controller;

import common.constants.ActionTypes;
import server.StartServer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Client implements Runnable {
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    private boolean isLookingForMatch = false;
    private String clientID = "";
    private String username = "";

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.clientID = UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        while (StartServer.isServerRunning) {
            try {
                // read request from the client
                String messageFromClient = dataInputStream.readUTF();
                ActionTypes.ActionType type = ActionTypes.getActionTypeFromMessage(messageFromClient);
                switch (type) {
                    case LOGIN_USER:
                        onLoginUser(messageFromClient);
                        break;
                    case FIND_MATCH:
                        onFindMatchRequest();
                        break;
                    case START_SINGLE_MATCH_REQUEST:
                        onStartSingleMatch();
                        break;
                    case UPDATE_GAME_POSITION_REQUEST:
                        onUpdateGamePositionRequest(messageFromClient);
                        break;
                    case RESET_GAME_POSITION_REQUEST:
                        onResetGamePositionRequest(messageFromClient);
                        break;
                    case GAME_EVENT_TIMEOUT:
                        onGameTimeoutRequest(messageFromClient);
                        break;
//                    case GAME_EVENT_WIN:
//                        onGameTimeoutRequest(messageFromClient);
//                        break;
                    case INVALID:
                        System.out.println("ERROR: invalid type " + type);
                        break;
                    default:
                        System.out.println("ERROR: unknown type " + type);
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
        String username = splitted[1];

        // TODO: login check

        // save user id
        this.username = username;

        // send user data to
        sendDataToClient(ActionTypes.ActionType.LOGIN_USER.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + this.clientID);
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

            System.out.println("======joining roon");

            Room room = StartServer.roomManager.newRoom();
            // add clients to the new room
            room.addClient(this);
            room.addClient(opponent);

            // send confirmation that clients joined the room with id
            this.sendDataToClient(ActionTypes.ActionType.JOIN_ROOM.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + room.getData());
            opponent.sendDataToClient(ActionTypes.ActionType.JOIN_ROOM.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + room.getData());
            room.startGame();
        }
    }

    private void onUpdateGamePositionRequest(String message) {
        String[] splitted = message.split(";");
        String roomID = splitted[1];

        Room room = StartServer.roomManager.findRoomByID(roomID);
        if (room == null) {
            System.out.println("onUpdateGamePositionRequest#no room with id " + roomID);
            return;
        }

        room.updateClientPosition(clientID, splitted[2], splitted[3]);
    }

    private void onResetGamePositionRequest(String message) {
        String[] splitted = message.split(";");
        String roomID = splitted[1];

        Room room = StartServer.roomManager.findRoomByID(roomID);
        if (room == null) {
            System.out.println("onResetGamePositionRequest#no room with id " + roomID);
            return;
        }
        room.resetClientPosition(clientID);
    }

    private void onGameTimeoutRequest(String message) {
        String[] splitted = message.split(";");
        String roomID = splitted[1];

        Room room = StartServer.roomManager.findRoomByID(roomID);
        if (room == null) {
            // room could be already deleted
            return;
        }
        room.onTimeoutEvent();
    }

    private void onStartSingleMatch() {
        this.isLookingForMatch = false;

        Room room = StartServer.roomManager.newRoom();
        // add clients to the new room
        room.addClient(this);

        // send confirmation that clients joined the room with id
        this.sendDataToClient(ActionTypes.ActionType.JOIN_ROOM.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + room.getData());
        room.startGame();
    }

    public boolean isLookingForMatch() {
        return this.isLookingForMatch;
    }

    public String getID() {
        return this.clientID;
    }
    public String getUsername() {
        return this.username;
    }

    public String getClientData() {
        return getID() + ";" + getUsername();
    }

    public static String getEmptyClientData() {
        return ";";
    }
}
