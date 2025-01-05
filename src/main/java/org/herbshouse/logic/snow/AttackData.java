package org.herbshouse.logic.snow;

import org.herbshouse.logic.Point2D;

public class AttackData {
    private Point2D locationToFollow;

    private int counterDegrees;

    private int phase = 0;

    public Point2D getLocationToFollow() {
        return locationToFollow;
    }

    public void setLocationToFollow(Point2D locationToFollow) {
        this.locationToFollow = locationToFollow;
    }

    public int getCounterDegrees() {
        return counterDegrees++ % 360;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

}
