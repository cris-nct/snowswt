package org.herbshouse.logic.blackhole;

import java.util.List;
import org.herbshouse.audio.AudioPlayType;
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
    getStrategy().playAudio("blackhole.wav", AudioPlayType.BACKGROUND, 1f);
    getStrategy().playAudio("blackhole-3.wav", AudioPlayType.BACKGROUND, 1f);
    snowflake.setShowTrail(true);
    snowflake.setSpeed(0.3 + Math.random() * 0.3);
    snowflake.getSnowTail().setTailLength(50);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {
    if (snowflakeList != null) {
      snowflakeList.forEach(snowflake -> {
        snowflake.setShowTrail(false);
        snowflake.setSpeed(0.3 + Math.random() * 0.7);
      });
    }
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    if (isFinished(snowflake)) {
      snowflake.setShowTrail(false);
      snowflake.setShowHead(false);
      return snowflake.getLocation();
    } else {
      double angle = Utils.angleOfLine(getStrategy().getFlagsConfiguration().getMouseLoc(), snowflake.getLocation());
      Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), BlackHoleStrategy.BLACKHOLE_RADIUS, angle);
      AttackDataBlackHole attackData = getStrategy().getData(snowflake);
      attackData.setLocationToFollow(dest);
      double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), dest);
      return Utils.moveToDirection(snowflake.getLocation(), snowflake.getSpeed(), directionToTarget);
    }
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }

  @Override
  public boolean isFinished(Snowflake snowflake) {
    return super.isFinished(snowflake) || getStrategy().getData(snowflake).isStartedWhiteRing();
  }
}
