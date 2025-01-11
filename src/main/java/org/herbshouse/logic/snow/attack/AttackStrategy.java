package org.herbshouse.logic.snow.attack;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.data.AbstractAttackData;

public interface AttackStrategy<T extends AbstractAttackData> {

  Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake);

  int getMaxSnowflakesInvolved();

  void beforeStart(List<Snowflake> snowflakeList);

  void afterUpdate(List<Snowflake> snowflakeList);

  int getAttackType();

  T getData(Snowflake snowflake);

  boolean isStarted();

  FlagsConfiguration getFlagsConfiguration();

  Rectangle getScreenBounds();

  void shutdown();
}
