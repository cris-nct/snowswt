package org.herbshouse.logic.snow.attack.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.PhaseProcessor;

public abstract class AbstractAttackPhaseStrategy<T extends AbstractPhaseAttackData> implements
    AttackStrategy<T> {

  private final List<PhaseProcessor<T>> phases = new ArrayList<>();

  private PhaseProcessor<T> currentPhaseProcessor;

  private volatile boolean allArrivedToDestination;

  private boolean started = false;

  @SafeVarargs
  public final void addPhases(PhaseProcessor<T>... phases) {
    this.phases.addAll(Arrays.stream(phases).toList());
  }

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    this.currentPhaseProcessor = phases.getFirst();
    this.initPhase(snowflakeList);
    started = true;
  }

  private void initPhase(List<Snowflake> snowflakeList) {
    for (Snowflake snowflake : snowflakeList) {
      initPhase(snowflake);
    }
  }

  private void initPhase(Snowflake snowflake) {
    getData(snowflake).setPhase(currentPhaseProcessor.getCurrentPhaseIndex());
    currentPhaseProcessor.startPhase(snowflake);
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public abstract T getData(Snowflake snowflake);

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    return currentPhaseProcessor.computeLocation(snowflake);
  }

  @Override
  public void afterUpdate(List<Snowflake> snowflakeList) {
    allArrivedToDestination = true;
    for (Snowflake snowflake : snowflakeList) {
      T data = getData(snowflake);
      if (snowflake.isFreezed() || data.getLocationToFollow() == null) {
        continue;
      }
      allArrivedToDestination = currentPhaseProcessor.isFinished(snowflake);
      if (!allArrivedToDestination) {
        break;
      }
    }
    if (allArrivedToDestination) {
      currentPhaseProcessor = currentPhaseProcessor.getNextPhaseProcessor();
      initPhase(snowflakeList);
    }
  }

  public PhaseProcessor<T> getCurrentPhaseProcessor() {
    return currentPhaseProcessor;
  }
}
