
package org.herbshouse.logic;

public class Point2D {

  /** x coordinate */
  public double x = 0.0d;

  /** y coordinate */
  public double y = 0.0d;

  /**
   * Constructs a new Point2D
   */
  public Point2D() {
    // Nothing to do
  }

  /**
   * Constructs a new Point2D
   * 
   * @param x
   * @param y
   */
  public Point2D(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Constructs a new Point2D
   * 
   * @param point
   */
  public Point2D(Point2D point) {
    this.x = point.x;
    this.y = point.y;
  }

  /**
   * Gets the X
   * 
   * @return x
   */
  public double getX() {
    return this.x;
  }

  /**
   * Gets the Y
   * 
   * @return y
   */
  public double getY() {
    return this.y;
  }

  /**
   * Sets x
   * 
   * @param x
   *          New value for x
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Sets y
   * 
   * @param y
   *          New value for y
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * @see Object#clone()
   */
  @Override
  public Point2D clone() {
    return new Point2D(this.x, this.y);
  }

}