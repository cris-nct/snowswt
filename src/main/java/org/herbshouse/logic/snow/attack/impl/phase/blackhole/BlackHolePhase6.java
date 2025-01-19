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
    Point2D locationToFollow = new Point2D(getStrategy().getScreenBounds().width * Math.random(),
        getStrategy().getScreenBounds().height * Math.random()
    );
    double angle = Utils.angleOfLine(snowflake.getLocation(), locationToFollow);
    locationToFollow = Utils.moveToDirection(locationToFollow, 2500, angle);
    attackData.setLocationToFollow(locationToFollow);
    if (startTime == 0) {
      startTime = System.currentTimeMillis();
    }
    snowflake.setAlpha(255);
    snowflake.setSpeed(0.5 + Math.random());
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), getStrategy().getData(snowflake).getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), snowflake.getSpeed(), directionToTarget);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 6;
  }

  @Override
  public boolean isFinished(Snowflake snowflake) {
    if (snowflake.getIndividualStrategy() == null) {
      return super.isFinished(snowflake);
    } else {
      return (System.currentTimeMillis() - startTime) > 20000;
    }
  }

}
