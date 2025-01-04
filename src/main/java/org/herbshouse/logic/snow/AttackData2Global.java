package org.herbshouse.logic.snow;

import java.util.Timer;
import java.util.TimerTask;

public class AttackData2Global {

    private static final double INITIAL_MIN_PHASE = 0.4;
    private static final double INITIAL_MAX_PHASE = 3.0;
    private static final double INITIAL_COUNTER = 1.0;

    private double counterStepsPhase = INITIAL_COUNTER;

    private double phaseIncrement = INITIAL_MIN_PHASE;

    private boolean allArrivedToDestination;

    private final Timer timer;

    public AttackData2Global() {
        this.timer = new Timer("AttackData2GlobalTimer");
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (phaseIncrement == INITIAL_MAX_PHASE) {
                    phaseIncrement = INITIAL_MIN_PHASE;
                } else if (phaseIncrement == INITIAL_MIN_PHASE) {
                    phaseIncrement = INITIAL_MAX_PHASE;
                }
            }
        }, 5000, 10000);

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                counterStepsPhase = INITIAL_COUNTER;
            }
        }, 5000, 30000);
    }

    public void resetTimers() {
        counterStepsPhase = INITIAL_COUNTER;
        phaseIncrement = INITIAL_MIN_PHASE;
    }

    public void shutdown() {
        timer.cancel();
        timer.purge();
    }

    public double getCounterStepsPhase() {
        return counterStepsPhase;
    }

    public boolean isAllArrivedToDestination() {
        return allArrivedToDestination;
    }

    public void increaseCounter() {
        counterStepsPhase += phaseIncrement;
    }

    public void setAllArrivedToDestination(boolean allArrivedToDestination) {
        this.allArrivedToDestination = allArrivedToDestination;
    }
}
