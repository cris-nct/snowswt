package org.herbshouse.logic.snow.attack.attack1;

import java.util.List;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.strategies.AbstractNoPhaseAttackStrategy;

public class BigWormAttackStrategy extends AbstractNoPhaseAttackStrategy<AttackData1> {

  private final FlagsConfiguration flagsConfiguration;

  public BigWormAttackStrategy(FlagsConfiguration flagsConfiguration) {
    this.flagsConfiguration = flagsConfiguration;
  }

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    double directionToTarget;
    boolean move = true;
    if (prevSnowFlake == null) {
      directionToTarget = Utils.angleOfPath(snowflake.getLocation(),
          flagsConfiguration.getMouseLoc());
    } else {
      directionToTarget = Utils.angleOfPath(snowflake.getLocation(), prevSnowFlake.getLocation());
      if (Utils.isColliding(snowflake, prevSnowFlake)) {
        move = false;
      }
    }
    if (move) {
      double distance = Math.abs(
          Math.sin(Math.toRadians(snowflake.getAttackData2().getCounterDegrees())));
      return Utils.moveToDirection(snowflake.getLocation(), distance, directionToTarget);
    } else {
      return snowflake.getLocation();
    }
  }

  @Override
  public int getMaxSnowflakesInvolved() {
    return 1000;
  }

  @Override
  public void afterUpdate(List<Snowflake> snowflakeList) {

  }

  @Override
  public void shutdown() {

  }

  @Override
  public int getAttackType() {
    return 1;
  }

  @Override
  public AttackData1 getData(Snowflake snowflake) {
    return snowflake.getAttackData1();
  }

}
