package org.herbshouse.logic.snow.attack.impl.nonphase;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractNoPhaseAttackStrategy;
import org.herbshouse.logic.snow.data.AttackDataYinYang;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class YinYangAttackStrategy extends AbstractNoPhaseAttackStrategy<AttackDataYinYang> {

  private final Rectangle screenBounds;

  public YinYangAttackStrategy(Rectangle screenBounds) {
    this.screenBounds = screenBounds;
  }

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    super.beforeStart(snowflakeList);
    int circleRadius = 160;
    int index = 0;
    for (Snowflake snowflake : snowflakeList) {
      snowflake.setSize(30);
      snowflake.getSnowTail().setTailLength(100);
      snowflake.setShowTrail(true);
      snowflake.setShowHead(false);
      if (index == 0) {
        snowflake.setColor(new RGB(70, 70, 240));
        snowflake.setLocation(
            new Point2D(screenBounds.width / 2.0, screenBounds.height / 2.0 + circleRadius));
        getData(snowflake).setAngle(Math.PI);
      } else if (index == 1) {
        snowflake.setColor(new RGB(240, 90, 90));
        snowflake.setLocation(
            new Point2D(screenBounds.width / 2.0, screenBounds.height / 2.0 - circleRadius));
        getData(snowflake).setAngle(0);
      }
      index++;
    }
  }

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    int radius = 160;
    AttackDataYinYang data = getData(snowflake);
    Point2D middleScreen = new Point2D(screenBounds.width / 2.0, screenBounds.height / 2.0);
    final Point2D newLoc;
    if (data.getAngle() < 50) {
      newLoc = Utils.moveToDirection(middleScreen, radius, data.getAngle());
      data.setAngle(data.getAngle() + 0.03);
    } else {
      int nrShapes = (int) (data.getAngle() / 50);
      newLoc = Utils.moveToDirection(middleScreen,
          radius + radius * Math.sin(nrShapes * data.getAngle()),
          data.getAngle());
      data.setAngle(data.getAngle() + 0.01);
    }
    return newLoc;
  }

  @Override
  public int getMaxSnowflakesInvolved() {
    return 2;
  }

  @Override
  public AttackDataYinYang getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData(AttackDataYinYang.class.getSimpleName());
    if (data == null) {
      data = new AttackDataYinYang();
      snowflake.setData(data.getClass().getSimpleName(), data);
    }
    return (AttackDataYinYang) data;
  }

  @Override
  public FlagsConfiguration getFlagsConfiguration() {
    return null;
  }

  @Override
  public Rectangle getScreenBounds() {
    return screenBounds;
  }

  @Override
  public void afterUpdate(List<Snowflake> snowflakeList) {

  }

  @Override
  public void shutdown() {

  }

  @Override
  public int getAttackType() {
    return 4;
  }


}
