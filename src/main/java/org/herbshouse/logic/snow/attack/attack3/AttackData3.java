package org.herbshouse.logic.snow.attack.attack3;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.attack.AbstractAttackData;

public class AttackData3 extends AbstractAttackData {
    private double speedPhase1;

    private Point2D locationToFollow;

    private double counter;

    public double getSpeedPhase1() {
        return speedPhase1;
    }

    public void setSpeedPhase1(double speedPhase1) {
        this.speedPhase1 = speedPhase1;
    }

    public double getCounter() {
        return counter++;
    }

    public void setCounter(double counter) {
        this.counter = counter;
    }

    public Point2D getLocationToFollow() {
        return locationToFollow;
    }

    public void setLocationToFollow(Point2D locationToFollow) {
        this.locationToFollow = locationToFollow;
    }
}
