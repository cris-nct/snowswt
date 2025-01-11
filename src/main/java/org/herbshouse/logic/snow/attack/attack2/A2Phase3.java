package org.herbshouse.logic.snow.attack.attack2;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

public class A2Phase3 extends AbstractA2 {

  protected A2Phase3(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    super(flagsConfiguration, screenBounds);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData2 attackData = this.getData(snowflake);
    double dir = Utils.angleOfPath(getFlagsConfiguration().getMouseLoc(), snowflake.getLocation());
    attackData.setLocationToFollow(
        Utils.moveToDirection(getFlagsConfiguration().getMouseLoc(), 250, dir));
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 3;
  }

}
