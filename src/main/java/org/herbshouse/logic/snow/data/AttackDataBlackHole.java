package org.herbshouse.logic.snow.data;

import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;

public class AttackDataBlackHole extends AbstractPhaseAttackData {

  private double radius;

  private double angle;

  private boolean startedWhiteRing;

  public double getAngle() {
    return angle = angle + 0.03;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public void setStartedWhiteRing(boolean startedWhiteRing) {
    this.startedWhiteRing = startedWhiteRing;
  }

  public boolean isStartedWhiteRing() {
    return startedWhiteRing;
  }
}
