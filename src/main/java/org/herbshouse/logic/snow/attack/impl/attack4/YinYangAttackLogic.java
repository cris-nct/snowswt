package org.herbshouse.logic.snow.attack.impl.attack4;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractNoPhaseAttackStrategy;
import org.herbshouse.logic.snow.data.AttackData4;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class YinYangAttackLogic extends AbstractNoPhaseAttackStrategy<AttackData4> {

  private final Rectangle screenBounds;

  public YinYangAttackLogic(Rectangle screenBounds) {
    this.screenBounds = screenBounds;
  }

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    int radius = 160;
    AttackData4 data = getData(snowflake);
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
    super.beforeStart(snowflakeList);
    int circleRadius = 160;
    int index = 0;
    for (Snowflake snowflake : snowflakeList) {
      snowflake.setSize(30);
      snowflake.getSnowTail().setTailLength(100);
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
  public AttackData4 getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData("ATTACK4");
    if (data == null) {
      data = new AttackData4();
      snowflake.setData("ATTACK4", data);
    }
    return (AttackData4) data;
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
