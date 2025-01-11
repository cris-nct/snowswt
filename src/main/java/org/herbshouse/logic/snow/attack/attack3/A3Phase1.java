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
public class A3Phase1 extends AbstractPhaseProcessor<AttackData3> {

  protected A3Phase1(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    super(flagsConfiguration, screenBounds);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData3 attackData = this.getData(snowflake);
    Point2D dest = Utils.moveToDirection(getFlagsConfiguration().getMouseLoc(), 600,
        Math.toRadians(Math.random() * 360));
    attackData.setLocationToFollow(dest);
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    Point2D locationToFollow = snowflake.getAttackData3().getLocationToFollow();
    double distToTarget = Utils.distance(snowflake.getLocation(), locationToFollow);
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(), locationToFollow);
    Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), 0.5, directionToTarget);
    double func = Math.tanh(distToTarget * 0.5) * 5 / distToTarget;
    func = Math.max(func, 0.5);
    return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
  }

  @Override
  public AttackData3 getData(Snowflake snowflake) {
    return snowflake.getAttackData3();
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }

}
