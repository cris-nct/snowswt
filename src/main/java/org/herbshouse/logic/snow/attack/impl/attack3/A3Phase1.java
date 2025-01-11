package org.herbshouse.logic.snow.attack.impl.attack3;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackData3;

/**
 * Move snowflakes on a circle around mouse position. Snowflakes looks like fireworks
 */
public class A3Phase1 extends AbstractPhaseProcessor<AttackData3> {

  protected A3Phase1(AttackStrategy<AttackData3> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData3 attackData = getStrategy().getData(snowflake);
    Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), 600,
        Math.toRadians(Math.random() * 360));
    attackData.setLocationToFollow(dest);
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    Point2D locationToFollow = getStrategy().getData(snowflake).getLocationToFollow();
    double distToTarget = Utils.distance(snowflake.getLocation(), locationToFollow);
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(), locationToFollow);
    Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), 0.5, directionToTarget);
    double func = Math.tanh(distToTarget * 0.5) * 5 / distToTarget;
    func = Math.max(func, 0.5);
    return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }

}
