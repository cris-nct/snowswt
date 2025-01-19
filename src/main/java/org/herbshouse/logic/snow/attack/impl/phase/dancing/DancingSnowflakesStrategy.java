package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractAttackPhaseStrategy;
import org.herbshouse.logic.snow.data.AttackDataDancing;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class DancingSnowflakesStrategy extends AbstractAttackPhaseStrategy<AttackDataDancing> implements
    IAttack2Global {

  private static final double INITIAL_COUNTER = 1.0;

  private final Timer timer;
  private final FlagsConfiguration flagsConfiguration;
  private final Rectangle screenBounds;
  private double counterStepsPhase = INITIAL_COUNTER;
  private double initialMinPhase = 0.4;
  private double initialMaxPhase = 3.0;
  private double phaseIncrement = initialMinPhase;

  public DancingSnowflakesStrategy(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;

    A2Phase1 phase1 = new A2Phase1(this);
    A2Phase2 phase2 = new A2Phase2(this);
    A2Phase3 phase3 = new A2Phase3(this);
    A2Phase4 phase4 = new A2Phase4(this, this);

    phase1.setNextPhase(phase2);
    phase2.setNextPhase(phase3);
    phase3.setNextPhase(phase4);
    phase4.setNextPhase(phase4);

    this.addPhases(phase1, phase2, phase3, phase4);

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

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    super.beforeStart(snowflakeList);
    for (Snowflake snowflake : snowflakeList) {
      snowflake.getSnowTail().setTailLength(100);
    }
    this.updateIncrementsBounds(flagsConfiguration.getSnowingLevel());
  }

  private void updateIncrementsBounds(int snowingLevel) {
    switch (snowingLevel) {
      case 1, 2 -> {
        initialMinPhase = 1;
        initialMaxPhase = 2;
      }
      case 3 -> {
        initialMinPhase = 1;
        initialMaxPhase = 5;
      }
      case 4 -> {
        initialMinPhase = 1;
        initialMaxPhase = 6;
      }
      case 5 -> {
        initialMinPhase = 1;
        initialMaxPhase = 7;
      }
      case 6 -> {
        initialMinPhase = 1;
        initialMaxPhase = 8;
      }
      case 10 -> {
        initialMinPhase = 0.4;
        initialMaxPhase = 10;
      }
      default -> {
        initialMinPhase = 0.4;
        initialMaxPhase = 3;
      }
    }
    this.phaseIncrement = initialMinPhase;
  }

  private void resetTimers() {
    counterStepsPhase = INITIAL_COUNTER;
    phaseIncrement = initialMinPhase;
  }

  public void shutdown() {
    resetTimers();
    timer.cancel();
    timer.purge();
  }

  @Override
  public double getCounterSteps() {
    return counterStepsPhase += phaseIncrement;
  }

  @Override
  public int getMaxSnowflakesInvolved() {
    return 1000;
  }

  @Override
  public int getAttackType() {
    return 2;
  }

  @Override
  public AttackDataDancing getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData(AttackDataDancing.class.getSimpleName());
    if (data == null) {
      data = new AttackDataDancing();
      snowflake.setData(data.getClass().getSimpleName(), data);
      this.beforeStart(List.of(snowflake));
    }
    return (AttackDataDancing) data;
  }

  @Override
  public FlagsConfiguration getFlagsConfiguration() {
    return flagsConfiguration;
  }

  @Override
  public Rectangle getScreenBounds() {
    return screenBounds;
  }

}
