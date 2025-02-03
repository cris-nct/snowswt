package org.herbshouse.logic.snow.attack;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.data.AbstractAttackData;

public interface PhaseProcessor<T extends AbstractAttackData> {

  Point2D computeLocation(Snowflake snowflake);

  int getCurrentPhaseIndex();

  PhaseProcessor<T> getNextPhaseProcessor();

  void startPhase(Snowflake snowflake);

  void endPhase(List<Snowflake> snowflakeList);

  boolean isFinished(Snowflake snowflake);

}
