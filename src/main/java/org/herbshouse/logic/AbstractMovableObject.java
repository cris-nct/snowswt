package org.herbshouse.logic;

import org.eclipse.swt.graphics.RGB;

public abstract class AbstractMovableObject {
    private Point2D location;
    private int size;
    private double speed = 1;
    private RGB color = new RGB(255, 255, 255);
    private int alpha = 255;

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public int getSize() {
        return size;
    }

    public Point2D getLocation() {
        return location;
    }

    public RGB getColor() {
        return color;
    }
}
