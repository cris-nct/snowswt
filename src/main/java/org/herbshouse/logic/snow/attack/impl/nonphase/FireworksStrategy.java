package org.herbshouse.logic.snow.attack.impl.nonphase;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractNoPhaseAttackStrategy;
import org.herbshouse.logic.snow.data.AttackDataFirework;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class FireworksStrategy extends AbstractNoPhaseAttackStrategy<AttackDataFirework> {

  private final FlagsConfiguration flagsConfiguration;
  private final Rectangle screenBounds;

  public FireworksStrategy(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;
  }

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    super.beforeStart(snowflakeList);
    for (Snowflake snowflake : snowflakeList) {
      AttackDataFirework attackData = getData(snowflake);
      attackData.setOriginalLocation(snowflake.getLocation().clone());
      attackData.setCounter(0);
      attackData.setStepX(Math.random() + 0.2);
      attackData.setStepAngle(Math.random() * 0.2 + 0.2);
      attackData.setFactorY(400 + Math.random() * 600);
      attackData.setDirectionX(Math.random() < 0.5 ? 1 : -1);
    }
  }

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    AttackDataFirework attackData = getData(snowflake);
    Point2D newLoc = attackData.getOriginalLocation().clone();
    double sinusoidalValue = Math.sin(Math.toRadians(attackData.getAngle()));
    if (sinusoidalValue < 0 && flagsConfiguration.isAttack()) {
      snowflake.setIndividualStrategy(null);
      newLoc = snowflake.getLocation();
    } else if (sinusoidalValue <= -0.8) {
      newLoc.y = -screenBounds.y;
    } else {
      newLoc.x += attackData.getDirectionX() * attackData.getCounter();
      newLoc.y += attackData.getFactorY() * sinusoidalValue;
    }
    return newLoc;
  }

  @Override
  public int getMaxSnowflakesInvolved() {
    return 100;
  }

  @Override
  public void afterUpdate(List<Snowflake> snowflakeList) {

  }

  @Override
  public int getAttackType() {
    return 5;
  }

  @Override
  public AttackDataFirework getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData(AttackDataFirework.class.getSimpleName());
    if (data == null) {
      data = new AttackDataFirework();
      snowflake.setData(data.getClass().getSimpleName(), data);
      this.beforeStart(List.of(snowflake));
    }
    return (AttackDataFirework) data;
  }

  @Override
  public FlagsConfiguration getFlagsConfiguration() {
    return flagsConfiguration;
  }

  @Override
  public Rectangle getScreenBounds() {
    return null;
  }

  @Override
  public void shutdown() {

  }

}
