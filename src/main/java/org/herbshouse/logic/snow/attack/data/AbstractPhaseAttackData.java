package org.herbshouse.logic.snow.attack.data;

public abstract class AbstractPhaseAttackData extends AbstractAttackData {

  private int phase = 0;

  public void setPhase(int phase) {
    this.phase = phase;
  }

  public int getPhase() {
    return phase;
  }

}
