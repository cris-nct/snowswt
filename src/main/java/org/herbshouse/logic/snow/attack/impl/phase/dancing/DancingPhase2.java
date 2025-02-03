package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.AttackDataDancing;

/**
 * Move snowflakes on a circle around mouse position but more close to the mouse than A2Phase1
 */
public class DancingPhase2 extends DancingAbstract {

  protected DancingPhase2(AttackStrategy<AttackDataDancing> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataDancing attackData = getStrategy().getData(snowflake);
    Point2D mouseLoc = getStrategy().getFlagsConfiguration().getMouseLoc();
    double dir = Utils.angleOfLine(mouseLoc, snowflake.getLocation());
    attackData.setLocationToFollow(Utils.moveToDirection(mouseLoc, 150, dir));
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {

  }

  @Override
  public int getCurrentPhaseIndex() {
    return 2;
  }

}
