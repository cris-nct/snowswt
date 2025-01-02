package org.herbshouse.logic.snow;

import org.herbshouse.logic.Point2D;

public class AttackData {
    private Point2D locationToFollow;

    private int counter;

    public void setLocationToFollow(Point2D locationToFollow) {
        this.locationToFollow = locationToFollow;
    }

    public Point2D getLocationToFollow() {
        return locationToFollow;
    }

    public int getCounter() {
        return counter++ % 360;
    }
}
