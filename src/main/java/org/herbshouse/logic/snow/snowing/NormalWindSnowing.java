package org.herbshouse.logic.snow.snowing;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

public class NormalWindSnowing {

  private final Rectangle screenBounds;

  public NormalWindSnowing(Rectangle screenBounds) {
    this.screenBounds = screenBounds;
  }

  public Point2D computeNextLocation(Snowflake snowflake) {
    Point2D newLoc = snowflake.getLocation().clone();
    int startCriticalArea = screenBounds.height / 2 + 200;
    int endCriticalArea = startCriticalArea + 100;
    if (newLoc.y > startCriticalArea && newLoc.y < endCriticalArea) {
      newLoc.x += Utils.linearInterpolation(newLoc.x, 1, 4, screenBounds.width, 0);
    } else if (newLoc.y < endCriticalArea) {
      //noinspection SuspiciousNameCombination
      newLoc.x += Utils.linearInterpolation(newLoc.y, endCriticalArea, 2, 0, 0);
    }
    newLoc.x = Math.min(newLoc.x, screenBounds.width);
    newLoc.y -= snowflake.getSpeed();
    return newLoc;
  }

}
