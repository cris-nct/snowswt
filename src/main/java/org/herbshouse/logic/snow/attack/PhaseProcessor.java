package org.herbshouse.logic.snow.attack;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.data.AbstractAttackData;

public interface PhaseProcessor<T extends AbstractAttackData> {

  Point2D computeLocation(Snowflake snowflake);

  int getCurrentPhaseIndex();

  PhaseProcessor<T> getNextPhaseProcessor();

  void startPhase(Snowflake snowflake);

  boolean isFinished(Snowflake snowflake);
}
