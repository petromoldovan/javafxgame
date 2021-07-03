package server.controller;

import common.constants.Constants;
import network.entity.StateChange;
import network.entity.enums.FrogMove;
import server.game.Engine;
import server.logic.Server;
import server.model.Position;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Room {
    private static final String c1StartPositionX = "1";
    private static final String c1StartPositionY = "761";
    private static final String c2StartPositionX = "200";
    private static final String c2StartPositionY = "761";
    
    private final String id;
    private final Engine engine;
    private Client c1 = null;
    private Client c2 = null;

    // game related data
    private final ScheduledExecutorService gameTimer;
    private Position c1Position = new Position(c1StartPositionX, c1StartPositionY);
    private Position c2Position = new Position(c2StartPositionX, c2StartPositionY);

    private final ArrayList<Client> participants = new ArrayList<>();

    public Room(String id) {
        this.id = id;
        gameTimer = Executors.newSingleThreadScheduledExecutor();
        engine = new Engine();
    }

    /**
     *   Trigger game start with constant updates
     */
    public void startGame() {
        engine.start(c2 != null, this::onChange);
    }

    private void onChange(final StateChange stateChange) {
        try {
            update(stateChange);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void stopGame() {
        gameTimer.shutdown();
        engine.stop();
    }

    public boolean addClient(Client c) {
        // check if room is full
        if (participants.size() >= 2) {
            return false;
        }

        if (participants.contains(c)) {
            return false;
        }

        participants.add(c);
        if (c1 == null) {
            c1 = c;
        } else {
            c2 = c;
        }

        return true;
    }

    public String getID() {
        return this.id;
    }

    public String getData() {
        String data = "";

        // get room id
        data += this.id + ";";

        // get player info
        data += getClientMetaInformation();

        // get timer
//        data += ";" + remainingGameTime;

        // get position info
        data += getPositionInformation();

        return data;
    }

    private String getClientMetaInformation() {
        String data = "";
        if (c1 != null) {
            data += c1.getClientData();
        } else {
            data += c1.getEmptyClientData();
        }

        data += ";";
        if (c2 != null) {
            data += c2.getClientData();
        } else {
            data += c2.getEmptyClientData();
        }
        return data;
    }

    private String getPositionInformation() {
        String data = ";";

        data += c1Position.toString();
        if (c2 != null) {
            data += ";" + c2Position.toString();
        }

        return data;
    }

    public void updateClientPosition(String clientID, FrogMove move) {
//        System.out.printf("clientId [%s] c1 [%s] c2 [%s]\n", clientID, c1==null ? null : c1.getID(), c2==null ? null : c2.getID());
        boolean isFirst = isFirstPlayer(clientID);
        boolean win = engine.updatePlayer(isFirst, move);
        if (win) onWin(isFirst);
    }

    private void onWin(final boolean first) {
        final Client winner = first ? c1 : c2;
        final Client loser = first ? c2 : c1;
        onWin(first, winner, loser);
    }

    public void resetClientPosition(String clientID) {
        if (isFirstPlayer(clientID)) {
            c1Position = new Position(c1StartPositionX, c1StartPositionY);
        } else if (isSecondPlayer(clientID)) {
            c2Position = new Position(c2StartPositionX, c2StartPositionY);
        }
    }

    private void gameOver() {
        stopGame();
        RoomManager.deleteRoomByID(id);
    }

    public void onTimeoutEvent() {
        for (Client c : participants) {
            c.onTimeout();
        }
    }

    private boolean isFirstPlayer(final String clientID) {
        return isPlayer(clientID, c1);
    }

    private boolean isSecondPlayer(final String clientID) {
        return isPlayer(clientID, c2);
    }

    private static boolean isPlayer(final String clientID, final Client client) {
        return client != null && client.getID().equals(clientID);
    }

    private void update(final StateChange change) {
        try {
            onUpdate(change);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void onUpdate(final StateChange change) {
        for (Client c : participants) {
            c.onStateChange(change);
        }
        if (change.getFrog1deaths() >= Constants.FROG_LIVES) {
            onWin(false, c2, c1);
        } else if (change.getFrog2deaths() >= Constants.FROG_LIVES) {
            onWin(true, c1, c2);
        }
    }

    private void onWin(final boolean first, final Client winner, final Client loser) {
//        int time = remainingGameTime.get();
        int time = 5; //todo fix
        int deaths = engine.getFrogDeaths(first);
        final int scores = Server.getLogic().calculateScores(time, deaths);
        if (null != winner) {
            Server.getLogic().saveScores(winner.getUsername(), scores);
            winner.winGame();
        }
        if (null != loser) {
            loser.loseGame();
        }
        gameOver();
    }
}
