package org.herbshouse.logic;

import org.eclipse.swt.graphics.RGB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snowflake {
    private Point2D location;
    private int size;
    private double speed = 1;
    private boolean freezed = false;
    private RGB color = new RGB(255,255,255);
    private final List<Point2D> historyLocations = new CopyOnWriteArrayList<>();

    public double getSpeed() {
        return speed;
    }

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
        return Collections.unmodifiableList(historyLocations);
    }

    public void registerHistoryLocation() {
        if (location != null) {
            historyLocations.add(new Point2D(location)); // Assuming Point2D has a copy constructor
        }
    }

    public void freeze() {
        if (!freezed) {
            freezed = true;
            color = new RGB(0, 255, 255);
        }
    }

    public boolean isFreezed() {
        return freezed;
    }
}