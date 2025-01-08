package org.herbshouse.logic.snow.attack.attack2;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseProcessor;

public class A2Phase2 extends AbstractPhaseProcessor<AttackData2> {

  protected A2Phase2(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    super(flagsConfiguration, screenBounds);
  }

  @Override
  protected void prepareNextPhase(Snowflake snowflake) {
    AttackData2 attackData = this.getData(snowflake);
    double dir = Utils.angleOfPath(snowflake.getLocation(), getFlagsConfiguration().getMouseLoc());
    attackData.setLocationToFollow(Utils.moveToDirection(snowflake.getLocation(), 150, dir));
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(),
        snowflake.getAttackData2().getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 2;
  }

  @Override
  public AttackData2 getData(Snowflake snowflake) {
    return snowflake.getAttackData2();
  }
}
