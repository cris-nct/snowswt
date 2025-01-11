package org.herbshouse.logic.snow.attack;

import org.herbshouse.logic.snow.data.AbstractAttackData;

public abstract class AbstractPhaseAttackData extends AbstractAttackData {

  private int phase = 0;

  public int getPhase() {
    return phase;
  }

  public void setPhase(int phase) {
    this.phase = phase;
  }

}
