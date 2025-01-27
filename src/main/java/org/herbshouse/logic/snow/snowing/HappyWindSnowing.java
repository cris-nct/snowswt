package org.herbshouse.logic.snow.snowing;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.data.HappyWindSnowFlakeData;

public class HappyWindSnowing {

  public Point2D computeNextLocation(Snowflake snowflake) {
    Point2D newLoc = snowflake.getLocation().clone();
    HappyWindSnowFlakeData data = getHappyWindData(snowflake);
    if (data.isMoveSinusoidal()) {
      newLoc.x = data.getOrigLocation().x + data.getAreaToMove() * Math.sin(data.getAngle());
    } else {
      newLoc.x += data.getStepX();
    }
    newLoc.y -= snowflake.getSpeed();
    return newLoc;
  }

  private HappyWindSnowFlakeData getHappyWindData(Snowflake snowflake) {
    HappyWindSnowFlakeData data = (HappyWindSnowFlakeData) snowflake.getData("HAPPYWIND");
    if (data == null) {
      data = new HappyWindSnowFlakeData();
      snowflake.setData("HAPPYWIND", data);
      this.initializeSnowFlakeHappyWind(snowflake);
    }
    return data;
  }

  public void initializeSnowFlakeHappyWind(Snowflake snowflake) {
    HappyWindSnowFlakeData data = getHappyWindData(snowflake);
    data.setOrigLocation(snowflake.getLocation().clone());
    final double speed;
    if (Math.random() > 0.6) {
      double part = Math.random();
      double stepX;
      if (part >= 0.33 && part <= 0.66) {
        stepX = 0;
        speed = 0.5 + Math.random();
      } else {
        stepX = 0.2 + 0.3 * Math.random();
        if (part < 0.33) {
          stepX = -stepX;
        }
        speed = Utils.linearInterpolation(Math.abs(stepX), 0, 0.5 + Math.random(), 1,
            1 + Math.random());
      }
      data.setStepX(stepX);
      data.setMoveSinusoidal(false);
    } else {
      speed = 0.5 + Math.random();
      data.setMoveSinusoidal(true);
      data.setAngleIncrease(Math.toRadians(0.2 * Math.random()));
      data.setAreaToMove(50 + (int) (Math.random() * 50));
    }
    snowflake.setSpeed(speed);
  }


}
