package org.herbshouse.logic.snow.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

import java.util.ArrayList;
import java.util.List;

public class SnowflakeAttack3Logic {

    private final List<IAttack3PhaseProcessor> phases = new ArrayList<>();

    private boolean allArrivedToDestination;

    public SnowflakeAttack3Logic(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {

        Phase0 phase0 = new Phase0(flagsConfiguration, screenBounds);
        Phase1 phase1 = new Phase1(flagsConfiguration, screenBounds);
        Phase2 phase2 = new Phase2(flagsConfiguration, screenBounds);
        Phase3 phase3 = new Phase3(flagsConfiguration, screenBounds);
        Phase4 phase4 = new Phase4(flagsConfiguration, screenBounds);
        Phase5 phase5 = new Phase5(flagsConfiguration, screenBounds);

        phase0.setNextPhase(phase1);
        phase1.setNextPhase(phase2);
        phase2.setNextPhase(phase3);
        phase3.setNextPhase(phase4);
        phase4.setNextPhase(phase5);
        phase5.setNextPhase(phase0);

        this.phases.add(phase0);
        this.phases.add(phase1);
        this.phases.add(phase2);
        this.phases.add(phase3);
        this.phases.add(phase4);
        this.phases.add(phase5);
    }

    public Point2D computeNextLocation(Snowflake snowflake) {
        AttackData3 data = snowflake.getAttackData3();
        Point2D newLoc = snowflake.getLocation();
        //noinspection OptionalGetWithoutIsPresent
        IAttack3PhaseProcessor phaseProcessor = phases.stream()
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
        this.allArrivedToDestination = true;
        for (Snowflake snowflake : snowflakeList) {
            if (snowflake.isFreezed() || snowflake.getAttackData3().getLocationToFollow() == null) {
                continue;
            }
            allArrivedToDestination = Utils.distance(snowflake.getLocation(), snowflake.getAttackData3().getLocationToFollow()) < 5;
            if (!allArrivedToDestination) {
                break;
            }
        }
    }

    public void setAllArrivedToDestination(boolean allArrivedToDestination) {
        this.allArrivedToDestination = allArrivedToDestination;
    }
}
