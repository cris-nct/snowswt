package org.herbshouse.logic.snow.data;

import org.herbshouse.logic.Point2D;

public class AttackData5 extends AbstractAttackData {

  private double angle;

  private double counter;

  private double stepAngle = 0.3;

  private double stepX = 1;

  private double factorY = 300;

  private int directionX = 1;

  private Point2D originalLocation;

  public Point2D getOriginalLocation() {
    return originalLocation;
  }

  public void setOriginalLocation(Point2D loc) {
    this.originalLocation = loc;
  }

  public void setStepX(double stepX) {
    this.stepX = stepX;
  }

  public double getAngle() {
    return angle += stepAngle;
  }

  public void setStepAngle(double stepAngle) {
    this.stepAngle = stepAngle;
  }

  public double getCounter() {
    return counter += stepX;
  }

  public void setCounter(double counter) {
    this.counter = counter;
  }

  public double getFactorY() {
    return factorY;
  }

  public void setFactorY(double factorY) {
    this.factorY = factorY;
  }

  public int getDirectionX() {
    return directionX;
  }

  public void setDirectionX(int directionX) {
    this.directionX = directionX;
  }

}

