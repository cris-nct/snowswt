package org.herbshouse.logic.enemies;

import org.eclipse.swt.graphics.RGB;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;

public class AnimatedGif extends AbstractMovableObject {

  private final double animationSpeed;
  private final RGB removeBackgroundColor;
  private final String filename;
  private double imageIndex;

  public AnimatedGif(
      String filename,
      double animationSpeed,
      RGB removeBackgroundColor
  ) {
    this.filename = filename;
    this.animationSpeed = animationSpeed;
    this.removeBackgroundColor = removeBackgroundColor;
  }

  public String getFilename() {
    return filename;
  }

  public RGB getRemoveBackgroundColor() {
    return removeBackgroundColor;
  }

  public int getImageIndex() {
    return (int) imageIndex;
  }

  public void increaseImageIndex() {
    imageIndex += animationSpeed;
  }

  public void move() {
    this.setLocation(new Point2D(getLocation().x, getLocation().y + getSpeed()));
  }
}
