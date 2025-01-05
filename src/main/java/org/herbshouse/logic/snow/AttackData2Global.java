package org.herbshouse.logic.snow;

import java.util.Timer;
import java.util.TimerTask;

public class AttackData2Global {

    private static final double INITIAL_COUNTER = 1.0;

    private double counterStepsPhase = INITIAL_COUNTER;

    private double initialMinPhase = 0.4;

    private double initialMaxPhase = 3.0;

    private boolean allArrivedToDestination;

    private double phaseIncrement = initialMinPhase;

    private final Timer timer;

    public AttackData2Global() {
        this.timer = new Timer("AttackData2GlobalTimer");
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (phaseIncrement == initialMaxPhase) {
                    phaseIncrement = initialMinPhase;
                } else if (phaseIncrement == initialMinPhase) {
                    phaseIncrement = initialMaxPhase;
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

    public void updateIncrementsBounds(int snowingLevel) {
        switch (snowingLevel) {
            case 1 -> {
                initialMinPhase = 1;
                initialMaxPhase = 1;
            }
            case 2 -> {
                initialMinPhase = 1;
                initialMaxPhase = 2;
            }
            //TODO
            case 3 -> {

            }
            case 10 -> {
                initialMinPhase = 0.1;
                initialMaxPhase = 1;
            }
            default -> {
                initialMinPhase = 0.4;
                initialMaxPhase = 3;
            }
        }
        this.phaseIncrement = initialMinPhase;
    }

    public void resetTimers() {
        counterStepsPhase = INITIAL_COUNTER;
        phaseIncrement = initialMinPhase;
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
