package org.herbshouse.logic.blackhole;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

public class BlackHolePhase1BornToWhite extends AbstractPhaseProcessor<AttackDataBlackHole> {

  protected BlackHolePhase1BornToWhite(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    snowflake.setLocation(((BlackHoleStrategy) getStrategy()).generateRandomPointOutside());
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    double angle = Utils.angleOfLine(getStrategy().getFlagsConfiguration().getMouseLoc(), snowflake.getLocation());
    Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), BlackHoleStrategy.BLACKHOLE_RADIUS, angle);
    attackData.setLocationToFollow(dest);
    getStrategy().playAudio("blackhole.wav");
    getStrategy().playAudio("blackhole-3.wav");
    snowflake.setShowTrail(true);
    snowflake.getSnowTail().setTailLength(50);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {
    if (snowflakeList != null) {
      snowflakeList.forEach(s -> s.setShowTrail(false));
    }
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    if (isFinished(snowflake)) {
      snowflake.setShowTrail(false);
      snowflake.setShowHead(false);
      return snowflake.getLocation();
    } else {
      double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), getStrategy().getData(snowflake).getLocationToFollow());
      return Utils.moveToDirection(snowflake.getLocation(), snowflake.getSpeed(), directionToTarget);
    }
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }


}
