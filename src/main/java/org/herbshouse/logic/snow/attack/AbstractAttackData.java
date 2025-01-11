package org.herbshouse.logic.snow.attack;

import org.herbshouse.logic.Point2D;

public abstract class AbstractAttackData {

  private int phase = 0;

  private Point2D locationToFollow;

  public int getPhase() {
    return phase;
  }

  public void setPhase(int phase) {
    this.phase = phase;
  }

  public void setLocationToFollow(Point2D locationToFollow) {
    this.locationToFollow = locationToFollow;
  }

  public Point2D getLocationToFollow() {
    return locationToFollow;
  }

}
