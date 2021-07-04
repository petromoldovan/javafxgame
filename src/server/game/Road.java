package server.game;

import common.constants.Constants;
import network.entity.StateChange;
import network.entity.enums.FrogMove;
import network.model.Car;
import network.model.Frog;
import network.model.enums.CarType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static common.constants.Constants.*;

public class Road {

    private static final int SIZE = 10;
    private static final int MIDDLE = SIZE / 2;
    private static final int SYNC_MS = 700;

    private final int width;
    private final List<LinkedList<Car>> cars = new ArrayList<>(SIZE);
    private final double[] carSpeed = new double[SIZE];
    private final int[] blockedDirection = new int[SIZE];
    private final int[] lineY = new int[SIZE];
    private final AtomicInteger frog1deaths = new AtomicInteger(0);
    private final AtomicInteger frog2deaths = new AtomicInteger(0);
    private final AtomicInteger frog1scores = new AtomicInteger(0);
    private final AtomicInteger frog2scores = new AtomicInteger(0);
    private Frog frog1;
    private Frog frog2;
    private final AtomicInteger frog1DeathTimer = new AtomicInteger(0);
    private final AtomicInteger frog2DeathTimer = new AtomicInteger(0);
    private final AtomicBoolean frog1DeathTimerActive = new AtomicBoolean(false);
    private final AtomicBoolean frog2DeathTimerActive = new AtomicBoolean(false);
    private final AtomicInteger roundTimer = new AtomicInteger(Constants.GAME_TIME * 1000);
    private final AtomicInteger roundTimerSync = new AtomicInteger(Constants.GAME_TIME * 1000 + SYNC_MS);
    private volatile StateChange externalChange;
    private final ThreadLocal<Random> random = ThreadLocal.withInitial(() -> new Random(System.nanoTime()));

    public Road(int blockSize, int width) {
        this.width = width;
        for (int i = 0; i < SIZE; i++) {
            cars.add(new LinkedList<>());
        }
        lineY[0] = TOP_ROAD_START;
        for (int i = 1; i < MIDDLE; i++) {
            final int y = lineY[i - 1] + blockSize;
            lineY[i] = y;
        }
        lineY[MIDDLE] = BOTTOM_ROAD_START;
        for (int i = MIDDLE + 1; i < SIZE; i++) {
            final int y = lineY[i - 1] + blockSize;
            lineY[i] = y;
        }
    }

    public void addCar(final int id) {
        final Random rnd = random.get();
//        Optional<CarType> typeOpt = CarType.random(rnd);
//        if (typeOpt.isEmpty()) return;
//        CarType type = typeOpt.get();
        CarType type = CarType.random(rnd);
        int line = rnd.nextInt(SIZE);
        Car car = new Car(id, type, rnd.nextDouble() > 0.5 ? type.getSpeed() : -type.getSpeed());
        car.setPosition(car.isMovingToTheRight() ? -car.getWidth() : WIDTH, lineY[line]);
        LinkedList<Car> carsOnLine = cars.get(line);
        if (car.isMovingToTheRight()) {
            if (carSpeed[line]>=0) {
                Car left = carsOnLine.isEmpty() ? null : carsOnLine.getFirst();
                if (blockedDirection[line]<=0 && !car.overlapsOnTheLeft(left)) {
                    double newSpeed = carSpeed[line]==0 
                            ? car.getSpeed() 
                            : Math.min(car.getSpeed(), carSpeed[line]);
                    car.setSpeed(newSpeed);
                    carSpeed[line] = newSpeed;
                    blockedDirection[line] = 0;
                    carsOnLine.addFirst(car);
                }
            } else {
                blockedDirection[line] = -1;
            }
        } else {
            if (carSpeed[line]<=0) {
                Car right = carsOnLine.isEmpty() ? null : carsOnLine.getLast();
                if (blockedDirection[line]>=0 && !car.overlapsOnTheRight(right)) {
                    double newSpeed = carSpeed[line]==0 
                            ? car.getSpeed() 
                            : Math.max(car.getSpeed(), carSpeed[line]);
                    car.setSpeed(newSpeed);
                    carSpeed[line] = newSpeed;
                    blockedDirection[line] = 0;
                    carsOnLine.addLast(car);
                }
            } else {
                blockedDirection[line] = 1;
            }
        }
    }

    public StateChange update() {
        List<Car> removal = new ArrayList<>();
        for (int line = 0; line < SIZE; line++) {
            removal.clear();
            LinkedList<Car> carsOnLine = cars.get(line);
            for (Car car : carsOnLine) {
                double x = car.getX();
                if (x < -car.getWidth() || x > width) {
                    car.setX(width);
                    removal.add(car);
                } else {
                    car.update();
                }
            }
            carsOnLine.removeAll(removal);
            if (carsOnLine.isEmpty()) {
                carSpeed[line] = 0;
            } else {
                if (carsOnLine.getFirst().getSpeed() > 0) {
                    final OptionalDouble min = carsOnLine.stream().mapToDouble(Car::getSpeed).min();
                    carSpeed[line] = min.orElse(0);
                } else {
                    final OptionalDouble max = carsOnLine.stream().mapToDouble(Car::getSpeed).max();
                    carSpeed[line] = max.orElse(0);
                }
            }
        }
        StateChange change = new StateChange();
        change.getCarRemoval().addAll(removal);
        for (int i = 0; i < SIZE; i++) {
            change.getCars().addAll(cars.get(i));
        }
        if (externalChange != null) {
            Frog frog1change = externalChange.getFrog1();
            if (frog1change != null) {
                frog1.set(frog1change.getX(), frog1change.getY());
                change.setFrog1(frog1);
            }
            if (externalChange.hasFrog1Deaths()) {
                change.setFrog1deaths(externalChange.getFrog1deaths());
            }
            Frog frog2change = externalChange.getFrog2();
            if (frog2change != null) {
                frog2.set(frog2change.getX(), frog2change.getY());
                change.setFrog2(frog2);
            }
            if (externalChange.hasFrog2Deaths()) {
                change.setFrog2deaths(externalChange.getFrog2deaths());
            }
            if (externalChange.hasFrog1Scores()) {
                change.setFrog1scores(externalChange.getFrog1scores());
            }
            if (externalChange.hasFrog2Scores()) {
                change.setFrog2scores(externalChange.getFrog2scores());
            }
            externalChange = null; // frog can move only in 1 game tick
        }
        for (int i = 0; i < SIZE; i++) {
            for (Car car : cars.get(i)) { //todo frog line
                frogCarInteraction(frog1, change, car);
                frogCarInteraction(frog2, change, car);
            }   
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
        if (change.hasFrog1Deaths() || change.hasFrog2Deaths()) System.out.println(change);
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
            int deaths = frog1deaths.incrementAndGet();
            change.setFrog1deaths(deaths);
            startDeathTimer(frog1DeathTimerActive, frog1DeathTimer);
            if (frog2 != null) frog2.set(START_X2, START_Y);
        } else {
            int deaths = frog2deaths.incrementAndGet();
            change.setFrog2deaths(deaths);
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

    public void updateFrog(boolean first, FrogMove move) {
        Frog frog = getFrog(first);
        if (frog1DeathTimerActive.get() || frog2DeathTimerActive.get() || externalChange!=null) return;
        double x = frog.getX();
        double y = frog.getY();
//        System.out.printf("Frog x=%f y=%f\n", x, y);
        boolean changed = false;
        StateChange change = new StateChange();
        switch (move) {
            case UP:
                y = y - BLOCK_SIZE;
                changed = true;
                if (y < FINISH) {
                    setDead(first ? frog2 : frog1, change);
                    setScores(first, Constants.FINISH_SCORE, change);
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
        if (!changed) return;
        if (first) {
            frog1.set(x, y);
            change.setFrog1(frog1); 
        } else {
            frog2.set(x, y);
            change.setFrog2(frog2);
        }
        externalChange = change;
    }

    private void setScores(final boolean first, final int score, final StateChange change) {
        if (first) {
            change.setFrog1scores(frog1scores.accumulateAndGet(score, Integer::sum));
            if (frog2 == null) change.setFrog2deaths(FROG_LIVES);
        } else change.setFrog2scores(frog2scores.accumulateAndGet(score, Integer::sum));
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
