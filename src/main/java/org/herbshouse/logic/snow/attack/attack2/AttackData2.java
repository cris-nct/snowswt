package org.herbshouse.logic.snow.attack.attack2;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.attack.AbstractAttackData;

public class AttackData2 extends AbstractAttackData {
    private Point2D locationToFollow;

    private int counterDegrees;

    public Point2D getLocationToFollow() {
        return locationToFollow;
    }

    public void setLocationToFollow(Point2D locationToFollow) {
        this.locationToFollow = locationToFollow;
    }

    public int getCounterDegrees() {
        return counterDegrees++ % 360;
    }

}
