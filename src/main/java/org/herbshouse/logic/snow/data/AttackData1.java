package org.herbshouse.logic.snow.data;

public class AttackData1 extends AbstractAttackData {

  private int counterDegrees;

  public int getCounterDegrees() {
    return counterDegrees++ % 360;
  }
}
