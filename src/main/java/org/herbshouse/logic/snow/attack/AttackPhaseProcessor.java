package org.herbshouse.logic.snow.attack;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;

public interface AttackPhaseProcessor<T extends AbstractAttackData> {

  Point2D computeLocation(Snowflake snowflake);

  int getCurrentPhaseIndex();

  AttackPhaseProcessor<T> getNextPhaseProcessor();

  T getData(Snowflake snowflake);

  void startPhase(Snowflake snowflake);
}
