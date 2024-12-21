
package org.herbshouse.logic;

public class Point2D {

  public double x = 0.0d;

  public double y = 0.0d;

  public Point2D() {}

  public Point2D(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Point2D(Point2D point) {
    this.x = point.x;
    this.y = point.y;
  }

  @Override
  public Point2D clone() {
    return new Point2D(this.x, this.y);
  }

}