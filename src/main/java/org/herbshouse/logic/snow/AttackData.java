package org.herbshouse.logic.snow;

import org.herbshouse.logic.Point2D;

public class AttackData {
    private Point2D locationToFollow;

    private int counterDegrees;

    private int phase = 0;

    public void setLocationToFollow(Point2D locationToFollow) {
        this.locationToFollow = locationToFollow;
    }

    public Point2D getLocationToFollow() {
        return locationToFollow;
    }

    public int getCounterDegrees() {
        return counterDegrees++ % 360;
    }

    public void setPhase(int phase){
        this.phase = phase;
    }

    public int getPhase() {
        return phase;
    }

}
