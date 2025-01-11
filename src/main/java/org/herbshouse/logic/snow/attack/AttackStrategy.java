package org.herbshouse.logic.snow.attack;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;

public interface AttackStrategy<T extends AbstractAttackData> {

  Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake);

  int getMaxSnowflakesInvolved();

  void beforeStart(List<Snowflake> snowflakeList);

  void afterUpdate(List<Snowflake> snowflakeList);

  int getAttackType();

  T getData(Snowflake snowflake);

  boolean isStarted();

  void shutdown();
}
