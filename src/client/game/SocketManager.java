package client.game;

import client.StartClient;
import client.controller.GameController;
import network.entity.LoginResponse;
import client.game.model.Player;
import network.entity.RegistrationResponse;
import network.entity.Scores;
import client.screen.AppScreen;
import com.google.gson.Gson;
import common.constants.ActionTypes;
import javafx.application.Platform;

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
    private final ExecutorService pool = Executors.newCachedThreadPool();
    
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Thread listener = null;
    String roomID = null;
    String clientID = null;
    private Class<?> controller;

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
                    case CURRENT_GAME_DATA_RESPONSE:
                        onCurrentGameDataResponse(messageFromServer);
                        break;
                    case SCORES:
                        onScoresResponse(messageFromServer);
                        break;
                    case REGISTER_USER:
                        onRegisterUser(messageFromServer);
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

    private String getJson(final String message) {
        final int i = message.indexOf(";");
        if (i < 0) return "";
        return message.substring(i + 1);
    }

    private void sendDataToServer(String data) {
        try {
            dataOutputStream.writeUTF(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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

    public void updateGamePosition(int x, int y) {
        sendDataToServer(ActionTypes.ActionType.UPDATE_GAME_POSITION_REQUEST.name() + ";" + this.roomID + ";" + x + ";" + y);
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
                    StartClient.gameScreenController.show();
                    Player p1 = new Player(splitted[3], splitted[4]);
                    GameController.setPlayer1(p1);
                    String p2ID = splitted[5];
                    if (!p2ID.equals("")) {
                        System.out.println("setting second player...");
                        Player p2 = new Player(splitted[5], splitted[6]);
                        GameController.setPlayer2(p2);
                    }
                    // set the frog that is controlled by the current client
                    if (p1.getID().equals(this.clientID)) {
                        System.out.println("I AM THE FIRST CLIUENT!");
                        GameController.isControllingFirstFrog(true);
                    } else {
                        System.out.println("I AM THE FIRST SECOND CLEINT!");
                        // user is controlling the second frog
                        GameController.isControllingFirstFrog(false);
                    }
                    GameController.startGame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("ERROR: onGetMultiplayerMatchInfoResponse# smth is wrong");
        }
    }

    private String onCurrentGameDataResponse(String message) {
        String[] splitted = message.split(";");
        int timeLeft = Integer.parseInt(splitted[6]);

        String player2ID = splitted[4];
//        System.out.println("got data " + message);
        Platform.runLater(
                () -> {
                    try {
                        GameController.setTimeLeft(timeLeft);
                        // set player position
                        GameController.setX1(splitted[7]);
                        GameController.setY1(splitted[8]);

                        // add second player
                        if (!player2ID.equals("")) {
                            GameController.setX2(splitted[9]);
                            GameController.setY2(splitted[10]);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        return message;
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
