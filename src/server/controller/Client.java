package server.controller;

import com.google.gson.Gson;
import common.constants.ActionTypes;
import network.entity.*;
import server.StartServer;
import server.logic.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;

import static common.constants.ActionTypes.ActionType.CURRENT_GAME_DATA_RESPONSE;

public class Client implements Runnable {
    
    private final Gson gson = new Gson();
    
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    private boolean isLookingForMatch = false;
    private final String clientID;
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
                String message = dataInputStream.readUTF();
                ActionTypes.ActionType type = ActionTypes.getActionTypeFromMessage(message);
                switch (type) {
                    case LOGIN_USER:
                        onLoginUser(message);
                        break;
                    case FIND_MATCH:
                        onFindMatchRequest();
                        break;
                    case START_SINGLE_MATCH_REQUEST:
                        onStartSingleMatch();
                        break;
                    case UPDATE_GAME_POSITION_REQUEST:
                        onUpdateGamePositionRequest(message);
                        break;
                    case SCORES:
                        onScores(message);
                        break;
                    case REGISTER_USER:
                        onRegisterUser(message);
                        break;
                    case INVALID:
                        System.out.println("ERROR: invalid type " + type);
                        break;
                    default:
                        System.out.println("ERROR: unknown type " + type);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                break;
            }
        }
    }

    private void onRegisterUser(final String message) {
        final String action = getActionPart(message);
        String[] data = message.split(";");
        if (data.length < 3) {
            final String err = "Error registering user: no username or password!";
            System.err.println(err);
            RegistrationResponse response = new RegistrationResponse(ActionTypes.Code.ERROR, err);
            sendDataToClient(action + gson.toJson(response));
            return;
        }
        String username = data[1];
        String password = data[2];
        final Optional<String> result = Server.getLogic().register(username, password);
        final ActionTypes.Code code = result.isEmpty() ? ActionTypes.Code.SUCCESS : ActionTypes.Code.ERROR;
        RegistrationResponse response = new RegistrationResponse(code, result.orElse(""));
        sendDataToClient(action + gson.toJson(response));
    }

    private void onScores(final String message) {
        final String action = getActionPart(message);
        final Scores scores = Server.getLogic().getScores();
        sendDataToClient(action + gson.toJson(scores));
    }

    private String getActionPart(final String message) {
        return message.split(";")[0] + ";";
    }

    public String sendDataToClient(String data) {
        try {
            // TODO: encrypt data?
            this.dataOutputStream.writeUTF(data);
            return "SUCCESS";
        } catch (IOException e) {
            e.printStackTrace();
            return "FAILURE";
        }
    }

    private void onLoginUser(String message) {
        final String action = getActionPart(message);
        String[] request = message.split(";");
        if (request.length < 3) {
            String err = "Error login in: no login supplied!";
            System.err.println(err);
            LoginResponse response = new LoginResponse(ActionTypes.Code.ERROR, err, clientID);
            sendDataToClient(action + gson.toJson(response));
            return;
        }
        username = request[1];
        String password = request[2];
        final Optional<String> err = Server.getLogic().login(username, password);
        final ActionTypes.Code code = err.isEmpty() ? ActionTypes.Code.SUCCESS : ActionTypes.Code.ERROR;
        final LoginResponse response = new LoginResponse(code, err.orElse(""), clientID);
        sendDataToClient(action + gson.toJson(response));
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
            room.addClient(opponent); // first client
            room.addClient(this); // second client who has found a match

            // send confirmation that clients joined the room with id
            this.sendDataToClient(ActionTypes.ActionType.JOIN_ROOM.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + room.getData());
            opponent.sendDataToClient(ActionTypes.ActionType.JOIN_ROOM.name() + ";" + ActionTypes.Code.SUCCESS.name() + ";" + room.getData());
            room.startGame();
        }
    }

//    private void onUpdateGamePositionRequest(String message) {
//        String[] splitted = message.split(";");
//        String roomID = splitted[1];
//
//        Room room = StartServer.roomManager.findRoomByID(roomID);
//        if (room == null) {
//            System.out.println("onUpdateGamePositionRequest#no room with id " + roomID);
//            return;
//        }
//        System.out.printf("update game position, client [%s] room: [%s] x=%s y=%s\n", clientID, roomID, splitted[2], splitted[3]);
//        room.updateClientPosition(clientID, splitted[2], splitted[3]);
//    }
    private void onUpdateGamePositionRequest(String message) {
        String[] s = message.split(";");
        if (s.length < 2) {
            System.err.println("Error updating game position malformed message: " + message);
            return;
        }
        String json = s[1];
        FrogMovementRequest request = gson.fromJson(json, FrogMovementRequest.class);
        String roomId = request.getRoomId();
        Room room = StartServer.roomManager.findRoomByID(roomId);
        if (room == null) {
            System.out.println("onUpdateGamePositionRequest#no room with id " + roomId);
            return;
        }
//        System.out.printf("move=%s client [%s] room: [%s] \n", request.getMove(), clientID, roomId);
        room.updateClientPosition(clientID, request.getMove());
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
        System.out.printf("starting single player, room id [%s]\n", room.getID());
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
    
    public void onStateChange(final StateChange change) {
        final String json = gson.toJson(change);
        sendDataToClient(CURRENT_GAME_DATA_RESPONSE.name() + ';' + json);
    }
    
    public void winGame() {
        send(ActionTypes.ActionType.GAME_EVENT_WIN);
    }

    public void loseGame() {
        send(ActionTypes.ActionType.GAME_EVENT_LOSE);
    }

    public void onTimeout() {
        send(ActionTypes.ActionType.GAME_EVENT_TIMEOUT);
    }

    private void send(final ActionTypes.ActionType action) {
        sendDataToClient(action.name() + ';');
    }
}
