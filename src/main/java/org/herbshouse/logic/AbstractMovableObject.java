package org.herbshouse.logic;

import org.eclipse.swt.graphics.RGB;

public abstract class AbstractMovableObject {

  private Point2D location;
  private int size;
  private double speed = 1;
  private RGB color = new RGB(255, 255, 255);
  private int alpha = 255;
  private boolean pause = false;

  public boolean isPause() {
    return pause;
  }

  public void setPause(boolean pause) {
    this.pause = pause;
  }

  public int getAlpha() {
    return alpha;
  }

  public void setAlpha(int alpha) {
    this.alpha = alpha;
  }

  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
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
}
