package org.herbshouse.logic.snow.attack.attack2;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

public class A2Phase4 extends AbstractA2 {

  private final IAttack2Global attack2Global;

  protected A2Phase4(FlagsConfiguration flagsConfiguration, Rectangle screenBounds,
      IAttack2Global attack2Global) {
    super(flagsConfiguration, screenBounds);
    this.attack2Global = attack2Global;
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData2 attackData = this.getData(snowflake);
    double direction =
        attack2Global.getCounterSteps() * Math.toRadians(attackData.getCounterDegrees());
    attackData.setLocationToFollow(
        Utils.moveToDirection(getFlagsConfiguration().getMouseLoc(), 450, direction));
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 4;
  }

}
