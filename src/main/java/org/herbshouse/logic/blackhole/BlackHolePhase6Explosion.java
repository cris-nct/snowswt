package org.herbshouse.logic.blackhole;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.herbshouse.audio.AudioPlayType;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

/**
 * Blackholw explosion
 */
public class BlackHolePhase6Explosion extends AbstractPhaseProcessor<AttackDataBlackHole> {

  protected BlackHolePhase6Explosion(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    attackData.setLocationToFollow(((BlackHoleStrategy) getStrategy()).generateRandomPointOutside());
    snowflake.setAlpha(255);
    snowflake.setSpeed(0.1 + Math.random() * 0.7);
    snowflake.setSize(snowflake.getSize() * 2);
    snowflake.setColor(new RGB(255, 255, 0));
    snowflake.getSnowTail().setTailLength(100);
    getStrategy().playAudio("explosion.wav", AudioPlayType.EFFECT, 1f);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {
    getStrategy().stopAudio("explosion.wav");
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

}
