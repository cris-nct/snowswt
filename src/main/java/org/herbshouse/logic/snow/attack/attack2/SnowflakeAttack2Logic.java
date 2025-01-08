package org.herbshouse.logic.snow.attack.attack2;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.IAttackPhaseProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SnowflakeAttack2Logic implements IAttack2Global {

    private static final double INITIAL_COUNTER = 1.0;

    private final List<IAttackPhaseProcessor<AttackData2>> phases = new ArrayList<>();
    private final Timer timer;
    private double counterStepsPhase = INITIAL_COUNTER;
    private double initialMinPhase = 0.4;
    private double initialMaxPhase = 3.0;
    private boolean allArrivedToDestination;
    private double phaseIncrement = initialMinPhase;

    public SnowflakeAttack2Logic(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        A2Phase0 phase0 = new A2Phase0(flagsConfiguration, screenBounds);
        A2Phase1 phase1 = new A2Phase1(flagsConfiguration, screenBounds);
        A2Phase2 phase2 = new A2Phase2(flagsConfiguration, screenBounds);
        A2Phase3 phase3 = new A2Phase3(flagsConfiguration, screenBounds, this);

        phase0.setNextPhase(phase1);
        phase1.setNextPhase(phase2);
        phase2.setNextPhase(phase3);

        this.phases.add(phase0);
        this.phases.add(phase1);
        this.phases.add(phase2);
        this.phases.add(phase3);

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

    public void setAllArrivedToDestination(boolean allArrivedToDestination) {
        this.allArrivedToDestination = allArrivedToDestination;
    }

    public void increaseCounter() {
        counterStepsPhase += phaseIncrement;
    }

    public Point2D computeNextLocation(Snowflake snowflake) {
        AttackData2 data = snowflake.getAttackData2();
        Point2D newLoc = snowflake.getLocation();
        //noinspection OptionalGetWithoutIsPresent
        IAttackPhaseProcessor<AttackData2> phaseProcessor = phases.stream()
                .filter(p -> p.getCurrentPhaseIndex() == data.getPhase())
                .findFirst().get();
        if (allArrivedToDestination || data.getLocationToFollow() == null) {
            phaseProcessor.initNextPhase(snowflake);
        } else {
            newLoc = phaseProcessor.computeLocation(snowflake);
        }
        return newLoc;
    }

    public void postProcessing(List<Snowflake> snowflakeList) {
        allArrivedToDestination = true;
        for (Snowflake snowflake : snowflakeList) {
            if (snowflake.isFreezed() || snowflake.getAttackData2().getLocationToFollow() == null) {
                continue;
            }
            allArrivedToDestination = Utils.distance(snowflake.getLocation(), snowflake.getAttackData2().getLocationToFollow()) < 5;
            if (!allArrivedToDestination) {
                break;
            }
        }
    }

    @Override
    public double getCounterSteps() {
        return counterStepsPhase += phaseIncrement;
    }

}
