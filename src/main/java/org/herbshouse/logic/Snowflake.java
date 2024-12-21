package org.herbshouse.logic;

import org.eclipse.swt.graphics.RGB;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snowflake {
    private Point2D location;
    private int size;
    private double speed = 1;
    private boolean freezed = false;
    private RGB color = new RGB(255,255,255);

    public double getSpeed() {
        return speed;
    }

    private final List<Point2D> historyLocations = new CopyOnWriteArrayList<>();

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public RGB getColor() {
        return color;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public List<Point2D> getHistoryLocations() {
        return historyLocations;
    }

    public void registerHistoryLocation() {
        this.historyLocations.add(location.clone());
    }

    public void setFreezed() {
        if (!freezed) {
            freezed = true;
            color = new RGB(0,255,255);
        }
    }

    public boolean isFreezed() {
        return freezed;
    }
}
