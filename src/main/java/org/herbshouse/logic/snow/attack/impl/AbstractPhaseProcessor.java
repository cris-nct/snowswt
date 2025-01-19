package org.herbshouse.logic.snow.attack.impl;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.PhaseProcessor;
import org.herbshouse.logic.snow.data.AbstractAttackData;

public abstract class AbstractPhaseProcessor<T extends AbstractAttackData> implements
    PhaseProcessor<T> {

  private final AttackStrategy<T> strategy;
  private AbstractPhaseProcessor<T> nextPhase;

  protected AbstractPhaseProcessor(AttackStrategy<T> strategy) {
    this.strategy = strategy;
  }

  public AttackStrategy<T> getStrategy() {
    return strategy;
  }

  public void setNextPhase(AbstractPhaseProcessor<T> nextPhase) {
    this.nextPhase = nextPhase;
  }

  @Override
  public AbstractPhaseProcessor<T> getNextPhaseProcessor() {
    return nextPhase;
  }

  @Override
  public boolean isFinished(Snowflake snowflake) {
    Point2D locationToFollow = getStrategy().getData(snowflake).getLocationToFollow();
    return locationToFollow == null || Utils.distance(snowflake.getLocation(), locationToFollow) < 5;
  }

}
