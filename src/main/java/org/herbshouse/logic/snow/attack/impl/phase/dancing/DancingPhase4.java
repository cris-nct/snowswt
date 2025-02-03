package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import java.util.List;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.AttackDataDancing;

public class DancingPhase4 extends DancingAbstract {

  private final IAttack2Global attack2Global;

  protected DancingPhase4(AttackStrategy<AttackDataDancing> strategy, IAttack2Global attack2Global) {
    super(strategy);
    this.attack2Global = attack2Global;
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataDancing attackData = getStrategy().getData(snowflake);
    double direction = attack2Global.getCounterSteps() * Math.toRadians(attackData.getCounterDegrees());
    attackData.setLocationToFollow(Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), 450, direction));
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {

  }

  @Override
  public int getCurrentPhaseIndex() {
    return 4;
  }

}
