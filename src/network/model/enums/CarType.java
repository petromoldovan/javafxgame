package network.model.enums;

import common.constants.Constants;

import java.util.Random;

public enum CarType {
    
    RED(0.4, 0.2), 
    YELLOW(0.5, 0.2),
    BLUE(0.6, 0.175),
    BLUE_RED(0.9, 0.125),
    YELLOW_STRIPE(1.1, 0.1),
    POLICE(2.5, 0.1),
    TRUCK(0.2, 0.1),
    ;
    
//    private static final List<CarType> TYPES;
//    
//    static {
//        TYPES = Arrays.asList(values());
//        TYPES.sort((t1, t2) -> Double.compare(t2.probability, t1.probability));
//    }

    final double speed;
    final double probability;
    CarType(final double speed, final double probability) {
        this.speed = speed * Constants.CAR_SPEED_MULTIPLIER;
        this.probability = probability;
    }

//    public static Optional<CarType> random(final Random random) {
//        for (CarType type : TYPES) {
//            if (random.nextDouble() < type.probability) return Optional.of(type);
//        }
//        return Optional.empty();
//    }
    public static CarType random(final Random random) {
        return values()[random.nextInt(values().length-1)];
    }

    public double getSpeed() {
        return speed;
    }
}
