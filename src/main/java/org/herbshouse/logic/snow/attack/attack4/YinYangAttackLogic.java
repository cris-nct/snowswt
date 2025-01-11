package org.herbshouse.logic.snow.attack.attack4;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;

public class YinYangAttackLogic implements AttackStrategy<YinYangData> {

  private final Rectangle screenBounds;

  private boolean started = false;

  public YinYangAttackLogic(Rectangle screenBounds) {
    this.screenBounds = screenBounds;
  }

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    int radius = 160;
    YinYangData data = snowflake.getAttackData4();
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
  public void beforeStart(List<Snowflake> snowflakeList) {
    int circleRadius = 160;
    int index = 0;
    for (Snowflake snowflake : snowflakeList) {
      snowflake.setSize(30);
      snowflake.getSnowTail().setTailLength(100);
      if (index == 0) {
        snowflake.setColor(new RGB(70, 70, 240));
        snowflake.setLocation(
            new Point2D(screenBounds.width / 2.0, screenBounds.height / 2.0 + circleRadius));
        snowflake.getAttackData4().setAngle(Math.PI);
      } else if (index == 1) {
        snowflake.setColor(new RGB(240, 90, 90));
        snowflake.setLocation(
            new Point2D(screenBounds.width / 2.0, screenBounds.height / 2.0 - circleRadius));
        snowflake.getAttackData4().setAngle(0);
      }
      index++;
    }
    started = true;
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

  @Override
  public YinYangData getData(Snowflake snowflake) {
    return snowflake.getAttackData4();
  }

  @Override
  public boolean isStarted() {
    return started;
  }
}
