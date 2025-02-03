package org.herbshouse.logic.snow.attack.impl.phase.parasites;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataParasites;

/**
 * Move snowflakes on a circle around mouse position. Snowflakes looks like fireworks
 */
public class ParasitesPhase1 extends AbstractPhaseProcessor<AttackDataParasites> {

  protected ParasitesPhase1(AttackStrategy<AttackDataParasites> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataParasites attackData = getStrategy().getData(snowflake);
    Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), 500,
        Math.toRadians(Math.random() * 360));
    attackData.setLocationToFollow(dest);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {

  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    Point2D locationToFollow = getStrategy().getData(snowflake).getLocationToFollow();
    double distToTarget = Utils.distance(snowflake.getLocation(), locationToFollow);
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), locationToFollow);
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
