package org.herbshouse.logic.snow.attack.impl.nonphase;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractNoPhaseAttackStrategy;
import org.herbshouse.logic.snow.data.AttackDataBigWorm;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class BigWormAttackStrategy extends AbstractNoPhaseAttackStrategy<AttackDataBigWorm> {

  private final FlagsConfiguration flagsConfiguration;

  public BigWormAttackStrategy(FlagsConfiguration flagsConfiguration) {
    this.flagsConfiguration = flagsConfiguration;
  }

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    double directionToTarget;
    boolean move = true;
    if (prevSnowFlake == null) {
      directionToTarget = Utils.angleOfLine(snowflake.getLocation(), flagsConfiguration.getMouseLoc());
    } else {
      directionToTarget = Utils.angleOfLine(snowflake.getLocation(), prevSnowFlake.getLocation());
      if (Utils.isColliding(snowflake, prevSnowFlake)) {
        move = false;
      }
    }
    if (move) {
      double distance = Math.abs(
          Math.sin(Math.toRadians(getData(snowflake).getCounterDegrees())));
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
  public AttackDataBigWorm getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData(AttackDataBigWorm.class.getSimpleName());
    if (data == null) {
      data = new AttackDataBigWorm();
      snowflake.setData(data.getClass().getSimpleName(), data);
    }
    return (AttackDataBigWorm) data;
  }

  @Override
  public FlagsConfiguration getFlagsConfiguration() {
    return flagsConfiguration;
  }

  @Override
  public Rectangle getScreenBounds() {
    return null;
  }

}
