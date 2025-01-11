package org.herbshouse.logic.snow.data;

import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;

public class AttackData3 extends AbstractPhaseAttackData {

  private double speedPhase1;

  private double counter;

  public double getSpeedPhase1() {
    return speedPhase1;
  }

  public void setSpeedPhase1(double speedPhase1) {
    this.speedPhase1 = speedPhase1;
  }

  public double getCounter() {
    return counter++;
  }

  public void setCounter(double counter) {
    this.counter = counter;
  }

}
