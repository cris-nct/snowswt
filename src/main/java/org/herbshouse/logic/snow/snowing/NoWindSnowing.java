package org.herbshouse.logic.snow.snowing;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;

public class NoWindSnowing {

  private final Rectangle screenBounds;

  public NoWindSnowing(Rectangle screenBounds) {
    this.screenBounds = screenBounds;
  }

  public Point2D computeNextLocation(Snowflake snowflake) {
    Point2D newLoc = snowflake.getLocation().clone();
    newLoc.x = Math.min(newLoc.x, screenBounds.width);
    newLoc.y -= snowflake.getSpeed();
    return newLoc;
  }

}
