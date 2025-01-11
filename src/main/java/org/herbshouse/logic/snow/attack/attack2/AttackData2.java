package org.herbshouse.logic.snow.attack.attack2;

import org.herbshouse.logic.snow.attack.data.AbstractPhaseAttackData;

public class AttackData2 extends AbstractPhaseAttackData {

  private int counterDegrees;

  public int getCounterDegrees() {
    return counterDegrees++ % 360;
  }

}
