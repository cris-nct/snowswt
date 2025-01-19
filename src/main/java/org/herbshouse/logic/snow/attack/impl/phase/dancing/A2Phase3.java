package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.AttackDataDancing;

public class A2Phase3 extends AbstractA2 {

  protected A2Phase3(AttackStrategy<AttackDataDancing> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataDancing attackData = getStrategy().getData(snowflake);
    Point2D mouseLoc = getStrategy().getFlagsConfiguration().getMouseLoc();
    double dir = Utils.angleOfLine(mouseLoc, snowflake.getLocation());
    attackData.setLocationToFollow(Utils.moveToDirection(mouseLoc, 250, dir));
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 3;
  }

}
