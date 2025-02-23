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

public class BlackHolePhase5RedRing extends AbstractPhaseProcessor<AttackDataBlackHole> {

  private long startTime = 0;

  protected BlackHolePhase5RedRing(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    attackData.setAngle(Utils.angleOfLine(snowflake.getLocation(), getStrategy().getFlagsConfiguration().getMouseLoc()));
    attackData.setRadius(BlackHoleStrategy.BLACKHOLE_RADIUS / 4 - BlackHoleStrategy.BLACKHOLE_RING_WIDTH * (1 - Math.random()));
    startTime = System.currentTimeMillis();
    //reddish
    snowflake.setColor(new RGB(201, 71, 0));
    snowflake.setSize(snowflake.getSize() / 2);
    snowflake.getSnowTail().setTailLength(50);
    getStrategy().playAudio("blackhole-2.wav", AudioPlayType.BACKGROUND, 1f);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {
    getStrategy().stopAudio("blackhole-2.wav");
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    snowflake.setAlpha(((BlackHoleStrategy) getStrategy()).getAlpha());
    return Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), attackData.getRadius(), attackData.getAngle());
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 5;
  }

  @Override
  public boolean isFinished(Snowflake snowflake) {
    return (System.currentTimeMillis() - startTime) > 20000;
  }

}