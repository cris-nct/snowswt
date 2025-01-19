package org.herbshouse.logic.snow.attack.impl.phase.blackhole;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

public class BlackHolePhase1 extends AbstractPhaseProcessor<AttackDataBlackHole> {

  protected BlackHolePhase1(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    double angle = Utils.angleOfLine(getStrategy().getFlagsConfiguration().getMouseLoc(), snowflake.getLocation());
    Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), BlackHoleStrategy.BLACKHOLE_RADIUS, angle);
    attackData.setLocationToFollow(dest);
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), getStrategy().getData(snowflake).getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }

}
