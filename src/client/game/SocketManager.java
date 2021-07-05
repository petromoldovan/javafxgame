package client.game;

import client.StartClient;
import client.game.model.Player;
import client.screen.AppScreen;
import com.google.gson.Gson;
import common.constants.ActionTypes;
import javafx.application.Platform;
import network.entity.*;
import network.entity.enums.FrogMove;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketManager {
    
    private final Gson gson = new Gson(); 
    private final Map<ActionTypes.ActionType, Object> data = new ConcurrentHashMap<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Thread listener = null;
    String roomID = null;
    String clientID = null;
    private Class<?> controller;
    private volatile boolean isRunning = true;

    public void connect(String host, int port) throws IOException {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 2000);

            System.out.println("Connected to server " + host + ":" + port);

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            listener = new Thread(this::readDataFromServer);
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized void start() {
        isRunning = true;
    }
    
    public synchronized void stop() {
        isRunning = false;
    }

    public void readDataFromServer() {

        while(isRunning()) {
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
                    case CURRENT_GAME_DATA_RESPONSE:
                        onCurrentGameDataResponse(messageFromServer);
                        break;
                    case SCORES:
                        onScoresResponse(messageFromServer);
                        break;
                    case REGISTER_USER:
                        onRegisterUser(messageFromServer);
                        break;
                    case GAME_EVENT_WIN:
                        onWinEvent(messageFromServer);
                        break;
                    case GAME_EVENT_LOSE:
                        onLoseEvent();
                        break;
                    case GAME_EVENT_TIMEOUT:
                        onTimeoutEvent();
                        break;
                    case INVALID:
                        System.out.println("ERROR: invalid type " + type);
                        break;
                    default:
                        System.out.println("ERROR: unknown type " + type);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onTimeoutEvent() {
        StartClient.getGameController().onTimeout();
    }

    private void onLoseEvent() {
        StartClient.getGameController().onLoseEvent();
    }

    private void onWinEvent(final String message) {
        StartClient.getGameController().onWinEvent();
    }

    private void onRegisterUser(final String message) {
        onResponse(message, ActionTypes.ActionType.REGISTER_USER, RegistrationResponse.class);
    }

    private void onScoresResponse(final String message) {
        onResponse(message, ActionTypes.ActionType.SCORES, Scores.class);
    }

    private <T> T onResponse(final String message, final ActionTypes.ActionType type, final Class<T> aClass) {
        String json = getJson(message);
        final T result = gson.fromJson(json, aClass);
        data.put(type, result);
        synchronized (type) {
            type.notifyAll();
        }
        System.out.println("response " + type + " " + json);
        return result;
    }
    
    private <T> T getResponse(final String message, final Class<T> aClass) {
        final String json = getJson(message);
        try {
            return gson.fromJson(json, aClass);
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.printf("Error parsing json [%s] to a class [%s]\n", json, aClass);
            throw new RuntimeException(t);
        }
    }

    private String getJson(final String message) {
        final int i = message.indexOf(";");
        if (i < 0) return "";
        return message.substring(i + 1);
    }

    private void sendDataToServer(String data) {
        try {
            dataOutputStream.writeUTF(data);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    // Requests to server

    public void login(String username, String password, Class<?> from, RunnableWithResult<LoginResponse> runnable) {
        validate(username, password);
        setFrom(from);
        sendRequest(runnable, ActionTypes.ActionType.LOGIN_USER, username, password);
    }

    public void findMatch() {
        String payload = ActionTypes.ActionType.FIND_MATCH + ";";
        sendDataToServer(payload);
    }

//    public void updateGamePosition(int x, int y) {
//        sendDataToServer(ActionTypes.ActionType.UPDATE_GAME_POSITION_REQUEST.name() + ";" + this.roomID + ";" + x + ";" + y);
//    }
    
    public void frogMove(FrogMove move) {
        FrogMovementRequest request = new FrogMovementRequest(roomID, move);
        String json = gson.toJson(request);
        sendDataToServer(ActionTypes.ActionType.UPDATE_GAME_POSITION_REQUEST.name() + ";" + json);
    }

    public void resetGamePosition() {
        sendDataToServer(ActionTypes.ActionType.RESET_GAME_POSITION_REQUEST.name() + ";" + this.roomID);
    }

    public void startSingleMatch() {
        String payload = ActionTypes.ActionType.START_SINGLE_MATCH_REQUEST.name() + ";";
        sendDataToServer(payload);
    }

    public void onGameTimeoutRequest() {
        String payload = ActionTypes.ActionType.GAME_EVENT_TIMEOUT.name() + ";" + this.roomID;
        sendDataToServer(payload);

        // clean room id
        this.roomID = "";
    }


    // Responses from server
    private void onLoginUserResponse(String message) {
        final LoginResponse response = onResponse(message, ActionTypes.ActionType.LOGIN_USER, LoginResponse.class);
        if (response.getCode() == ActionTypes.Code.SUCCESS) {
            this.clientID = response.getUid();
            AppScreen.DASHBOARD.goFrom(controller);
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

        if (status.equalsIgnoreCase(ActionTypes.Code.SUCCESS.name())) {
            this.roomID = splitted[2];

            System.out.println("message " + message);
            Platform.runLater(() -> {
                try {
                    // initialize the game
                    StartClient.getGameController().show();
                    Player p1 = new Player(splitted[3], splitted[4]);
                    StartClient.getGameController().setPlayer1(p1);
                    String p2ID = splitted[5];
                    if (!p2ID.equals("")) {
                        System.out.println("setting second player...");
                        Player p2 = new Player(splitted[5], splitted[6]);
                        StartClient.getGameController().setPlayer2(p2);
                    }
                    // set the frog that is controlled by the current client
                    if (p1.getID().equals(this.clientID)) {
                        System.out.println("I AM THE FIRST CLIENT!");
                        StartClient.getGameController().isControllingFirstFrog(true);
                    } else {
                        System.out.println("I AM THE SECOND CLIENT!");
                        // user is controlling the second frog
                        StartClient.getGameController().isControllingFirstFrog(false);
                    }
                    StartClient.getGameController().startGame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("ERROR: onGetMultiplayerMatchInfoResponse# smth is wrong");
        }
    }

    private void onCurrentGameDataResponse(String message) {
        StateChange change = getResponse(message, StateChange.class);
        Platform.runLater(() -> StartClient.getGameController().onChangeState(change));
    }

    public void showScores(RunnableWithResult<Scores> runnable) {
        sendRequest(runnable, ActionTypes.ActionType.SCORES);
    }

    public void registerUser(String username, String password, RunnableWithResult<RegistrationResponse> runnable) {
        validate(username, password);
        sendRequest(runnable, ActionTypes.ActionType.REGISTER_USER, username, password);
    }

    private void validate(final String username, final String password) {
        if (null == username || username.isBlank()) {
            throw new IllegalArgumentException("Incorrect username!");
        }
        if (null == password || password.isBlank()) {
            throw new IllegalArgumentException("Incorrect password!");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void sendRequest(final RunnableWithResult<T> runnable, final ActionTypes.ActionType actionType, String... requestData) {
        try {
            pool.submit(() -> {
                try {
                    final String request = createRequest(actionType, requestData);
                    sendDataToServer(request);
                    synchronized (actionType) {
                        actionType.wait();
                    }
                    final T result = (T) this.data.get(actionType);
                    runnable.execute(result);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private String createRequest(final ActionTypes.ActionType actionType, final String[] data) {
        StringBuilder sb = new StringBuilder(actionType.name());
        sb.append(';');
        if (data != null) {
            for (String s : data) {
                sb.append(s).append(';');
            }
        }
        return sb.toString();
    }

    private void setFrom(final Class<?> from) {
        controller = from;
    }
    
    public interface RunnableWithResult<T> {

        void execute(T data);
    }
}
