package org.herbshouse.logic.snow.attack.impl.attack2;

import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.AttackData2;

public class A2Phase4 extends AbstractA2 {

  private final IAttack2Global attack2Global;

  protected A2Phase4(AttackStrategy<AttackData2> strategy, IAttack2Global attack2Global) {
    super(strategy);
    this.attack2Global = attack2Global;
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData2 attackData = getStrategy().getData(snowflake);
    double direction =
        attack2Global.getCounterSteps() * Math.toRadians(attackData.getCounterDegrees());
    attackData.setLocationToFollow(
        Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), 450, direction));
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 4;
  }

}
