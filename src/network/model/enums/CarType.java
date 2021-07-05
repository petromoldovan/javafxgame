package network.model.enums;

import common.constants.Constants;

import java.util.Random;

public enum CarType {
    
    RED(0.4), 
    YELLOW(0.5),
    BLUE(0.6),
    BLUE_RED(0.9),
    YELLOW_STRIPE(1.1),
    POLICE(2.5),
    TRUCK(0.2),
    ;
    
    final double speed;
    CarType(final double speed) {
        this.speed = speed * Constants.CAR_SPEED_MULTIPLIER;
    }

    public static CarType random(final Random random) {
        return values()[random.nextInt(values().length) - 1];
    }

    public double getSpeed() {
        return speed;
    }
}
