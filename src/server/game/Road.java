package server.game;

import common.constants.Constants;
import network.entity.StateChange;
import network.entity.enums.FrogMove;
import network.model.Car;
import network.model.Frog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static common.constants.Constants.*;

public class Road {

    private static final int SIZE = 10;
    private static final int MIDDLE = SIZE / 2;
    private static final int SYNC_MS = 700;

    private final int width;
    private final int blockSize;
    private final List<Car> cars;
    private final double[] carSpeed = new double[SIZE];
    private final int[] lineY = new int[SIZE];
    private final AtomicInteger frog1deaths = new AtomicInteger(0);
    private final AtomicInteger frog2deaths = new AtomicInteger(0);
    private Frog frog1;
    private Frog frog2;
    private final AtomicInteger frog1DeathTimer = new AtomicInteger(0);
    private final AtomicInteger frog2DeathTimer = new AtomicInteger(0);
    private final AtomicBoolean frog1DeathTimerActive = new AtomicBoolean(false);
    private final AtomicBoolean frog2DeathTimerActive = new AtomicBoolean(false);
    private final AtomicInteger roundTimer = new AtomicInteger(Constants.GAME_TIME * 1000);
    private final AtomicInteger roundTimerSync = new AtomicInteger(Constants.GAME_TIME * 1000 + SYNC_MS);
    private volatile double frog1x;
    private volatile double frog1y;
    private volatile double frog2x;
    private volatile double frog2y;
    private final AtomicBoolean frog1moves = new AtomicBoolean(false);
    private final AtomicBoolean frog2moves = new AtomicBoolean(false);

    {
        int i = -1;
        carSpeed[++i] = -0.5;
        carSpeed[++i] = 0.5;
        carSpeed[++i] = -0.5;
        carSpeed[++i] = 0.5;
        carSpeed[++i] = -0.5;

        carSpeed[++i] = -0.2;
        carSpeed[++i] = 0.8;
        carSpeed[++i] = -0.3;
        carSpeed[++i] = 0.4;
        carSpeed[++i] = -0.5;
        
        for (i = 0; i < carSpeed.length; i++) {
            carSpeed[i] = carSpeed[i] * Constants.CAR_SPEED_MODIFIER;
        }
    }

    public Road(int blockSize, int width) {
        this.width = width;
        this.blockSize = blockSize;
        cars = new CopyOnWriteArrayList<>();
        lineY[0] = TOP_ROAD_START;
        for (int i = 1; i < MIDDLE; i++) {
            lineY[i] = lineY[i - 1] + blockSize;
        }
        lineY[MIDDLE] = BOTTOM_ROAD_START;
        for (int i = MIDDLE + 1; i < SIZE; i++) {
            lineY[i] = lineY[i - 1] + blockSize;
        }
    }

    public void add(Car car, int line) {
        car.setSpeed(carSpeed[line]);
        car.setPosition(-car.getWidth() - 1, lineY[line]);
        cars.add(car);
    }

    public StateChange update() {
        StateChange change = new StateChange();
        for (Car car : cars) {
            int x = (int) car.getX();
            if (x < -car.getWidth()) {
                car.setX(width);
                car.setSpawn(true);
            } else if (x > width) {
                car.setX(-blockSize);
                car.setSpawn(true);
            } else {
                car.setSpawn(false);
            }
            car.update();
        }
        change.getCars().addAll(cars);
        if (frog1moves.get()) {
            frog1.set(frog1x, frog1y);
            frog1moves.set(false);
            change.setFrog1(frog1);
        }
        if (frog2moves.get()) {
            frog2.set(frog2x, frog2y);
            frog2moves.set(false);
            change.setFrog2(frog2);
        }
        for (Car car : cars) {
            frogCarInteraction(frog1, change, car);
            frogCarInteraction(frog2, change, car);
        }
        if (!frog1DeathTimerActive.get() && !frog2DeathTimerActive.get()) {
            // time is out
            if (roundTimer.accumulateAndGet(-GAME_TICK_TIMER, Integer::sum) <= 0) {
                setDead(frog1, change);
                setDead(frog2, change);
            }
        }
        frogDeathTimer(change, frog1DeathTimerActive, frog1DeathTimer, frog1);
        frogDeathTimer(change, frog2DeathTimerActive, frog2DeathTimer, frog2);
        if (roundTimer.get() < roundTimerSync.get()) {
            roundTimerSync.set(roundTimer.get() - SYNC_MS);
            change.setTime(roundTimer.get());
        }
        return change;
    }

    private void frogDeathTimer(StateChange change, AtomicBoolean active, AtomicInteger timer, Frog frog) {
        if (!active.get()) return;
        if (timer.get() > 0) {
            timer.accumulateAndGet(-Constants.GAME_TICK_TIMER, Integer::sum);
        } else {
            frog.setDead(false);
            frog1.set(START_X1, START_Y);
            change.setFrog1(frog1);
            if (null != frog2) {
                change.setFrog2(frog2);
                frog2.set(START_X2, START_Y);
            }
            active.set(false);
        }
    }

    private void frogCarInteraction(Frog frog, StateChange change, final Car car) {
        if (null==frog || frog.isDead() || !car.hits(frog)) return;
        setDead(frog, change);
    }

    private void setDead(final Frog frog, final StateChange change) {
        if (null == frog) return;
        frog.setDead(true);
        if (frog.isFirst()) {
            int lives = frog1deaths.incrementAndGet();
            change.setFrog1deaths(lives);
            startDeathTimer(frog1DeathTimerActive, frog1DeathTimer);
            if (frog2 != null) frog2.set(START_X2, START_Y);
        } else {
            int lives = frog2deaths.incrementAndGet();
            change.setFrog2deaths(lives);
            startDeathTimer(frog2DeathTimerActive, frog2DeathTimer);
            frog1.set(START_X1, START_Y);
        }
        change.setFrog1(frog1);
        if (frog2 != null) change.setFrog2(frog2);
        roundTimer.set(Constants.GAME_TIME * 1000);
        roundTimerSync.set(roundTimer.get() - SYNC_MS);
        change.setTime(roundTimer.get());
    }

    private void startDeathTimer(final AtomicBoolean active, final AtomicInteger deathTimer) {
        active.set(true);
        deathTimer.set(Constants.FROG_DEAD_TIME);
    }

    public boolean updateFrog(boolean first, FrogMove move) {
        boolean result = false;
        Frog frog = getFrog(first);
        if (frog1DeathTimerActive.get() || frog2DeathTimerActive.get() || isMoving(frog)) return result;
        double x = frog.getX();
        double y = frog.getY();
//        System.out.printf("Frog x=%f y=%f\n", x, y);
        boolean changed = false;
        switch (move) {
            case UP:
                y = y - BLOCK_SIZE;
                changed = true;
                if (y <= FINISH) {
                    // win condition met
                    result = true;
                }
                break;
            case DOWN:
                y = y + BLOCK_SIZE;
                if (y < HEIGHT) changed = true;
                break;
            case LEFT:
                x = x - FROG_SIZE;
                if (x >= 0) changed = true;
                break;
            case RIGHT:
                x = x + FROG_SIZE;
                if (x < WIDTH - FROG_SIZE) changed = true;
                break;
        }
        if (!changed) return result;
        if (first) {
            synchronized (frog1moves) {
                frog1x = x;
                frog1y = y;
                frog1moves.set(true);
            }
        } else {
            synchronized (frog2moves) {
                frog2x = x;
                frog2y = y;
                frog2moves.set(true);
            }
        }
        return result;
    }

    private boolean isMoving(final Frog frog) {
        return frog == frog1 ? frog1moves.get() : frog2moves.get();
    }

    private Frog getFrog(final boolean first) {
        return first ? frog1 : frog2;
    }

    public synchronized Frog addFrog() {
        boolean isFirst = frog1 == null;
        Frog frog;
        if (isFirst) {
            frog1 = new Frog(true, START_X1, START_Y);
            frog = frog1;
        } else {
            frog2 = new Frog(false, START_X2, START_Y);
            frog = frog2;
        }
        return frog;
    }

    public int getFrogDeaths(final boolean first) {
        return first ? frog1deaths.get() : frog2deaths.get();
    }
}
