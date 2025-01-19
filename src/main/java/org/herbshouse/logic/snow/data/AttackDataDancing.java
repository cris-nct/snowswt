package org.herbshouse.logic.snow.data;

import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;

public class AttackDataDancing extends AbstractPhaseAttackData {

  private int counterDegrees;

  public int getCounterDegrees() {
    return counterDegrees++ % 360;
  }

}
