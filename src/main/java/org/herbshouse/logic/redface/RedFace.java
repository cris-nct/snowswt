package org.herbshouse.logic.redface;

import org.herbshouse.logic.AbstractMovableObject;

public class RedFace extends AbstractMovableObject {
    private double direction;

    private double imageIndex;

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getDirection() {
        return direction;
    }

    public int getImageIndex() {
        return (int) imageIndex;
    }

    public void increaseImageIndex() {
        imageIndex += 0.1;
    }

}
