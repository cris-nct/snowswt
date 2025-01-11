package org.herbshouse.logic.snow.attack.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseProcessor;

/**
 * Snoflakes joined together and follows mouse position
 */
public class A3Phase3 extends AbstractPhaseProcessor<AttackData3> {

  protected A3Phase3(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    super(flagsConfiguration, screenBounds);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData3 attackData = this.getData(snowflake);
    attackData.setSpeedPhase1(Math.random() / 2 + 0.3);
    attackData.setLocationToFollow(getFlagsConfiguration().getMouseLoc());
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    AttackData3 attackData3 = snowflake.getAttackData3();
    double distToTarget = Utils.distance(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(),
        attackData3.getLocationToFollow());
    Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
    double func = 2 * Math.tanh(distToTarget * 0.5);
    attackData3.setLocationToFollow(getFlagsConfiguration().getMouseLoc());
    return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
  }

  @Override
  public AttackData3 getData(Snowflake snowflake) {
    return snowflake.getAttackData3();
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 3;
  }

}
