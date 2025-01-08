package org.herbshouse.logic.snow.attack;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;

public interface IAttackPhaseProcessor<T extends AbstractAttackData> {

  Point2D computeLocation(Snowflake snowflake);

  int getCurrentPhaseIndex();

  void initNextPhase(Snowflake snowflake);

  IAttackPhaseProcessor<T> getNextPhaseProcessor();

  T getData(Snowflake snowflake);
}
