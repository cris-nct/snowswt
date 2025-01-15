package org.herbshouse.logic.snow.attack.impl.phase.parasites;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackData3;

/**
 * Move snowflakes on a circle around mouse position. Snowflakes looks like fireworks
 */
public class A3Phase4 extends AbstractPhaseProcessor<AttackData3> {

  protected A3Phase4(AttackStrategy<AttackData3> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData3 attackData = getStrategy().getData(snowflake);
    attackData.setLocationToFollow(
        new Point2D(getStrategy().getScreenBounds().width * Math.random(),
            getStrategy().getScreenBounds().height * Math.random()));
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    AttackData3 attackData3 = getStrategy().getData(snowflake);
    double distToTarget = Utils.distance(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), attackData3.getSpeedPhase1(),
        directionToTarget);
    double func = Math.tanh(distToTarget * 0.5) * 5 / distToTarget;
    return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
  }


  @Override
  public int getCurrentPhaseIndex() {
    return 4;
  }

}
