package org.herbshouse.logic;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snowflake {
    private Point2D location;
    private int size;
    private double speed = 1;

    private boolean freezed = false;

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
            size *= 2;
        }
    }

    public boolean isFreezed() {
        return freezed;
    }
}
