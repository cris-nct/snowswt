package org.herbshouse.logic.snow.attack.attack2;

import org.herbshouse.logic.snow.attack.AbstractAttackData;

public class AttackData2 extends AbstractAttackData {

  private int counterDegrees;

  public int getCounterDegrees() {
    return counterDegrees++ % 360;
  }

}
