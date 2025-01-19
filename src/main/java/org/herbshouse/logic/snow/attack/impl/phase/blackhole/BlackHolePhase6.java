package org.herbshouse.logic.snow.attack.impl.phase.blackhole;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

/**
 * Blackholw explosion
 */
public class BlackHolePhase6 extends AbstractPhaseProcessor<AttackDataBlackHole> {

  private long startTime = 0;

  protected BlackHolePhase6(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    final double locX;
    final double locY;
    if (Math.random() < 0.5) {
      locX = Utils.linearInterpolation(Math.random(), 0, -2000, 1, -200);
    } else {
      locX = Utils.linearInterpolation(Math.random(), 0, 2000, 1, 4000);
    }
    if (Math.random() < 0.5) {
      locY = Utils.linearInterpolation(Math.random(), 0, -1200, 1, -200);
    } else {
      locY = Utils.linearInterpolation(Math.random(), 0, 1200, 1, 2400);
    }
    attackData.setLocationToFollow(new Point2D(locX, locY));
    if (startTime == 0) {
      startTime = System.currentTimeMillis();
    }
    snowflake.setAlpha(255);
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), getStrategy().getData(snowflake).getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 6;
  }

  @Override
  public boolean isFinished(Snowflake snowflake) {
    return (System.currentTimeMillis() - startTime) > 15000;
  }

}
