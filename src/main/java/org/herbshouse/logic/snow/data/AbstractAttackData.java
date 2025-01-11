package org.herbshouse.logic.snow.data;

import org.herbshouse.logic.Point2D;

public abstract class AbstractAttackData implements SnowflakeData {

  private Point2D locationToFollow;

  public Point2D getLocationToFollow() {
    return locationToFollow;
  }

  public void setLocationToFollow(Point2D locationToFollow) {
    this.locationToFollow = locationToFollow;
  }

}
