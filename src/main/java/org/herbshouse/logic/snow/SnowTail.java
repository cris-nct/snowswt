package org.herbshouse.logic.snow;

import org.herbshouse.logic.CircularQueue;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class SnowTail {

  private final CircularQueue<Point2D> historyLocations = new CircularQueue<>(100);
  private final Snowflake snowflake;
  private Point2D prevPointRegistered;

  public SnowTail(Snowflake snowflake) {
    this.snowflake = snowflake;
  }

  public CircularQueue<Point2D> getHistoryLocations() {
    return historyLocations;
  }

  public void registerHistoryLocation() {
    if (snowflake.getLocation() != null &&
        (prevPointRegistered == null
            || Utils.distance(prevPointRegistered, snowflake.getLocation()) > 2)) {
      try {
        prevPointRegistered = new Point2D(snowflake.getLocation());
        historyLocations.offer(prevPointRegistered); // Assuming Point2D has a copy constructor
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }
  }

}
