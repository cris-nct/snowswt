package org.herbshouse.logic.snow.attack.data;

import org.herbshouse.logic.Point2D;

public abstract class AbstractAttackData {

  private Point2D locationToFollow;

  public void setLocationToFollow(Point2D locationToFollow) {
    this.locationToFollow = locationToFollow;
  }

  public Point2D getLocationToFollow() {
    return locationToFollow;
  }

}
