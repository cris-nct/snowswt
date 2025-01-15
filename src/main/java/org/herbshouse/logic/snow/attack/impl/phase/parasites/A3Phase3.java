package org.herbshouse.logic.snow.attack.impl.phase.parasites;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackData3;

/**
 * Snoflakes joined together and follows mouse position
 */
public class A3Phase3 extends AbstractPhaseProcessor<AttackData3> {

  protected A3Phase3(AttackStrategy<AttackData3> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData3 attackData = getStrategy().getData(snowflake);
    attackData.setSpeedPhase1(Math.random() / 2 + 0.3);
    attackData.setLocationToFollow(getStrategy().getFlagsConfiguration().getMouseLoc());
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    AttackData3 attackData3 = getStrategy().getData(snowflake);
    double distToTarget = Utils.distance(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
    double func = 2 * Math.tanh(distToTarget * 0.5);
    attackData3.setLocationToFollow(getStrategy().getFlagsConfiguration().getMouseLoc());
    return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
  }


  @Override
  public int getCurrentPhaseIndex() {
    return 3;
  }

}
