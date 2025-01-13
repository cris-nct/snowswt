package org.herbshouse.logic.snow.data;

import org.herbshouse.logic.Point2D;

public class HappyWindSnowFlakeData implements SnowflakeData {

  private volatile Point2D origLocation;
  private double angle;
  private double angleIncrease = 0.02;
  private int areaToMove;
  private double stepX = 0;
  private boolean moveSinusoidal = true;

  public void setMoveSinusoidal(boolean moveSinusoidal) {
    this.moveSinusoidal = moveSinusoidal;
  }

  public boolean isMoveSinusoidal() {
    return moveSinusoidal;
  }

  public void setStepX(double stepX) {
    this.stepX = stepX;
  }

  public double getStepX() {
    return stepX;
  }

  public int getAreaToMove() {
    return areaToMove;
  }

  public void setAreaToMove(int areaToMove) {
    this.areaToMove = areaToMove;
  }

  public void setAngleIncrease(double angleIncrease) {
    this.angleIncrease = angleIncrease;
  }

  public double getAngle() {
    return this.angle += angleIncrease;
  }

  public Point2D getOrigLocation() {
    return origLocation;
  }

  public void setOrigLocation(Point2D origLocation) {
    this.origLocation = origLocation;
  }

}
