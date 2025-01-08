package org.herbshouse.logic.snow.attack.attack3;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.IAttackPhaseProcessor;

public class SnowflakeAttack3Logic {

  private final List<IAttackPhaseProcessor<AttackData3>> phases = new ArrayList<>();

  private boolean allArrivedToDestination;

  public SnowflakeAttack3Logic(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    A3Phase0 phase0 = new A3Phase0(flagsConfiguration, screenBounds);
    A3Phase1 phase1 = new A3Phase1(flagsConfiguration, screenBounds);
    A3Phase2 phase2 = new A3Phase2(flagsConfiguration, screenBounds);
    A3Phase3 phase3 = new A3Phase3(flagsConfiguration, screenBounds);
    A3Phase4 phase4 = new A3Phase4(flagsConfiguration, screenBounds);
    A3Phase5 phase5 = new A3Phase5(flagsConfiguration, screenBounds);

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
    IAttackPhaseProcessor<AttackData3> phaseProcessor = phases.stream()
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
      allArrivedToDestination =
          Utils.distance(snowflake.getLocation(), snowflake.getAttackData3().getLocationToFollow())
              < 5;
      if (!allArrivedToDestination) {
        break;
      }
    }
  }

}
