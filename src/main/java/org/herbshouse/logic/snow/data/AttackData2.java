package org.herbshouse.logic.snow.data;

import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;

public class AttackData2 extends AbstractPhaseAttackData {

  private int counterDegrees;

  public int getCounterDegrees() {
    return counterDegrees++ % 360;
  }

}
