package org.herbshouse.logic.blackhole;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

public class BlackHolePhase3WhiteToYellow extends AbstractPhaseProcessor<AttackDataBlackHole> {

  protected BlackHolePhase3WhiteToYellow(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    double angle = Utils.angleOfLine(getStrategy().getFlagsConfiguration().getMouseLoc(), snowflake.getLocation());
    Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), BlackHoleStrategy.BLACKHOLE_RADIUS / 2, angle);
    attackData.setLocationToFollow(dest);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {

  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), getStrategy().getData(snowflake).getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 3;
  }

}
