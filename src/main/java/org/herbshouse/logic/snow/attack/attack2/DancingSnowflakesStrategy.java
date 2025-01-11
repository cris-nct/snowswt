package org.herbshouse.logic.snow.attack.attack2;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.strategies.AbstractAttackPhaseStrategy;

public class DancingSnowflakesStrategy extends AbstractAttackPhaseStrategy<AttackData2> implements
    IAttack2Global {

  private static final double INITIAL_COUNTER = 1.0;

  private final Timer timer;
  private double counterStepsPhase = INITIAL_COUNTER;
  private double initialMinPhase = 0.4;
  private double initialMaxPhase = 3.0;
  private double phaseIncrement = initialMinPhase;
  private final FlagsConfiguration flagsConfiguration;

  public DancingSnowflakesStrategy(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    this.flagsConfiguration = flagsConfiguration;

    A2Phase1 phase1 = new A2Phase1(flagsConfiguration, screenBounds);
    A2Phase2 phase2 = new A2Phase2(flagsConfiguration, screenBounds);
    A2Phase3 phase3 = new A2Phase3(flagsConfiguration, screenBounds);
    A2Phase4 phase4 = new A2Phase4(flagsConfiguration, screenBounds, this);

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
    return 350;
  }

  @Override
  public int getAttackType() {
    return 2;
  }

  @Override
  public AttackData2 getData(Snowflake snowflake) {
    return snowflake.getAttackData2();
  }

}
