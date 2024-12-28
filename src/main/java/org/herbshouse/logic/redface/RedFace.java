package org.herbshouse.logic.redface;

import org.herbshouse.logic.AbstractMovableObject;

public class RedFace extends AbstractMovableObject {
    private double direction;

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getDirection() {
        return direction;
    }
}
