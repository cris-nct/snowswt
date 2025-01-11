package org.herbshouse.logic.snow.attack.attack2;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

/**
 * Move snowflakes on a circle around mouse position.
 */
public class A2Phase1 extends AbstractA2 {

  protected A2Phase1(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    super(flagsConfiguration, screenBounds);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackData2 attackData = this.getData(snowflake);
    Point2D dest = Utils.moveToDirection(getFlagsConfiguration().getMouseLoc(), 250,
        Math.toRadians(Math.random() * 360));
    attackData.setLocationToFollow(dest);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }

}
