package org.herbshouse.logic.snow.attack.attack2;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseProcessor;

public class A2Phase3 extends AbstractPhaseProcessor<AttackData2> {

  private final IAttack2Global attack2Global;

  protected A2Phase3(FlagsConfiguration flagsConfiguration, Rectangle screenBounds,
      IAttack2Global attack2Global) {
    super(flagsConfiguration, screenBounds);
    this.attack2Global = attack2Global;
  }

  @Override
  protected void prepareNextPhase(Snowflake snowflake) {
    AttackData2 attackData = this.getData(snowflake);
    double direction =
        attack2Global.getCounterSteps() * Math.toRadians(attackData.getCounterDegrees());
    attackData.setLocationToFollow(
        Utils.moveToDirection(getFlagsConfiguration().getMouseLoc(), 250, direction));
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(),
        snowflake.getAttackData2().getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 3;
  }

  @Override
  public AttackData2 getData(Snowflake snowflake) {
    return snowflake.getAttackData2();
  }
}
