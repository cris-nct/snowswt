package org.herbshouse.logic;

import org.eclipse.swt.graphics.RGB;
import org.herbshouse.gui.GuiUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snowflake {
    private final List<Point2D> historyLocations = new CopyOnWriteArrayList<>();
    private Point2D location;
    private int size;
    private double speed = 1;
    private boolean freezed = false;
    private RGB color = new RGB(255, 255, 255);

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public RGB getColor() {
        return color;
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
            color = GuiUtils.FREEZE_COLOR;
        }
    }

    public boolean isFreezed() {
        return freezed;
    }
}