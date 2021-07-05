package server.game;

import network.entity.StateChange;
import network.entity.enums.FrogMove;
import network.model.Frog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static common.constants.Constants.*;

public class Engine {

    private static final int POOL_SIZE = 10;

    private ScheduledExecutorService pool;
    private Road road;
    private int id;
    private StateChangeEvent changeEvent;

    public synchronized void start(boolean twoPlayers, StateChangeEvent event) {
        changeEvent = event;
        road = new Road(BLOCK_SIZE, WIDTH);
        addFrogs(twoPlayers);
        pool = Executors.newScheduledThreadPool(POOL_SIZE);
        pool.scheduleWithFixedDelay(this::update, 0, GAME_TICK_TIMER, TimeUnit.MILLISECONDS);
    }

    private void addFrogs(boolean twoFrogs) {
        StateChange change = new StateChange();
        Frog frog1 = road.addFirstFrog();
        change.setFrog1(frog1);
        if (twoFrogs) {
            Frog frog2 = road.addSecondFrog();
            change.setFrog2(frog2);
        }
        changeEvent.onChange(change);
    }

    public synchronized void stop() {
        if (pool != null) pool.shutdown();
        pool = null;
        id = 0;
        changeEvent = null;
    }

    private void update() {
        try {
            if (++id == Integer.MAX_VALUE) id = 1;
            road.addCar(id);
            changeEvent.onChange(road.update());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void updatePlayer(final boolean isFirst, final FrogMove move) {
        road.updateFrog(isFirst, move);
    }

    public int getFrogDeaths(boolean first) {
        return road.getFrogDeaths(first);
    }
}
