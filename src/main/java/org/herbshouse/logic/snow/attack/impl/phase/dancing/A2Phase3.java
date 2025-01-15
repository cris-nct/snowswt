package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.AttackData2;

public class A2Phase3 extends AbstractA2 {

  protected A2Phase3(AttackStrategy<AttackData2> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData2 attackData = getStrategy().getData(snowflake);
    Point2D mouseLoc = getStrategy().getFlagsConfiguration().getMouseLoc();
    double dir = Utils.angleOfPath(mouseLoc, snowflake.getLocation());
    attackData.setLocationToFollow(Utils.moveToDirection(mouseLoc, 250, dir));
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 3;
  }

}
