package org.herbshouse.logic;

public class HappyWindSnowData {
    private volatile Point2D origLocation;
    private double angle;
    private double angleIncrease = 0.02;
    private int areaToMove;


    public void setAreaToMove(int areaToMove) {
        this.areaToMove = areaToMove;
    }

    public int getAreaToMove() {
        return areaToMove;
    }

    public void setAngleIncrease(double angleIncrease) {
        this.angleIncrease = angleIncrease;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }


    public double getAngle() {
        return angle;
    }

    public Point2D getOrigLocation() {
        return origLocation;
    }

    public void setOrigLocation(Point2D origLocation) {
        this.origLocation = origLocation;
    }

    public double getAngleIncrease() {
        return angleIncrease;
    }

    public void increaseAngle() {
        this.angle += angleIncrease;
    }

}
