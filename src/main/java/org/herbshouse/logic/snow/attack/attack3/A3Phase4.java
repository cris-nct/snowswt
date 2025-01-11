package org.herbshouse.logic.snow.attack.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.phase.AbstractPhaseProcessor;

/**
 * Move snowflakes on a circle around mouse position. Snowflakes looks like fireworks
 */
public class A3Phase4 extends AbstractPhaseProcessor<AttackData3> {

  protected A3Phase4(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    super(flagsConfiguration, screenBounds);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData3 attackData = this.getData(snowflake);
    attackData.setLocationToFollow(new Point2D(getScreenBounds().width * Math.random(),
        getScreenBounds().height * Math.random()));
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    AttackData3 attackData3 = snowflake.getAttackData3();
    double distToTarget = Utils.distance(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), attackData3.getSpeedPhase1(),
        directionToTarget);
    double func = Math.tanh(distToTarget * 0.5) * 5 / distToTarget;
    return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
  }

  @Override
  public AttackData3 getData(Snowflake snowflake) {
    return snowflake.getAttackData3();
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 4;
  }

}
