package org.herbshouse.logic.snow.attack.impl.attack2;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.AttackData2;

/**
 * Move snowflakes on a circle around mouse position.
 */
public class A2Phase1 extends AbstractA2 {

  protected A2Phase1(AttackStrategy<AttackData2> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData2 attackData = getStrategy().getData(snowflake);
    Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), 250,
        Math.toRadians(Math.random() * 360));
    attackData.setLocationToFollow(dest);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }

}
