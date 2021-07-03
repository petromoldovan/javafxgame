package server.game;

import network.entity.StateChange;
import network.entity.enums.FrogMove;
import network.model.Car;
import network.model.Frog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static common.constants.Constants.*;

public class Engine {

    private static final int POOL_SIZE = 10;

    private ScheduledExecutorService pool;
    private Road road;
    private AtomicInteger idGen;
    private StateChangeEvent changeEvent;

    public synchronized void start(boolean twoPlayers, StateChangeEvent event) {
        idGen = new AtomicInteger();
        changeEvent = event;
        road = new Road(BLOCK_SIZE, WIDTH);
        addFrogs(twoPlayers);
        pool = Executors.newScheduledThreadPool(POOL_SIZE);
        pool.scheduleWithFixedDelay(this::update, 0, GAME_TICK_TIMER, TimeUnit.MILLISECONDS);
        spawn(pool);
    }

    private void addFrogs(boolean twoFrogs) {
        StateChange change = new StateChange();
        Frog frog1 = road.addFrog();
        change.setFrog1(frog1);
        if (twoFrogs) {
            Frog frog2 = road.addFrog();
            change.setFrog1(frog2);
        }
        changeEvent.onChange(change);
    }

    private void spawn(final ScheduledExecutorService pool) {
        final TimeUnit ms = TimeUnit.MILLISECONDS;
        
        pool.schedule(() -> spawnCar(5), 0, ms);
        pool.schedule(() -> spawnCar(5), 5000, ms);
        pool.schedule(() -> spawnCar(5), 10000, ms);

        pool.schedule(() -> spawnCar(6), 0, ms);
        pool.schedule(() -> spawnCar(6), 2000, ms);

        pool.schedule(() -> spawnCar(7), 0, ms);
        pool.schedule(() -> spawnCar(7), 2500, ms);
        pool.schedule(() -> spawnCar(7), 5000, ms);
        pool.schedule(() -> spawnCar(7), 6500, ms);

        pool.schedule(() -> spawnCar(8), 500, ms);
        pool.schedule(() -> spawnCar(8), 4500, ms);
        pool.schedule(() -> spawnCar(8), 6300, ms);

        pool.schedule(() -> spawnCar(9), 300, ms);
        pool.schedule(() -> spawnCar(9), 2200, ms);
        pool.schedule(() -> spawnCar(9), 4500, ms);
    }

    public synchronized void stop() {
        if (pool != null) pool.shutdown();
        pool = null;
        idGen.set(0);
        changeEvent = null;
    }

    private void spawnCar(final int line) {
        Car car = new Car(idGen.incrementAndGet());
        road.add(car, line);
    }

    private void update() {
        try {
            final StateChange stateChange = road.update();
            changeEvent.onChange(stateChange);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean updatePlayer(final boolean isFirst, final FrogMove move) {
        return road.updateFrog(isFirst, move);
    }

    public int getFrogDeaths(boolean first) {
        return road.getFrogDeaths(first);
    }
}
