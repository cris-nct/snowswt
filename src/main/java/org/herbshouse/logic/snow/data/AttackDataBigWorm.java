package org.herbshouse.logic.snow.data;

public class AttackDataBigWorm extends AbstractAttackData {

  private int counterDegrees;

  public int getCounterDegrees() {
    return counterDegrees++ % 360;
  }
}
