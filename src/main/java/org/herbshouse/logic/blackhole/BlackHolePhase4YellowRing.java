package org.herbshouse.logic.blackhole;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

public class BlackHolePhase4YellowRing extends AbstractPhaseProcessor<AttackDataBlackHole> {

  private long startTime = 0;

  protected BlackHolePhase4YellowRing(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    attackData.setAngle(Utils.angleOfLine(snowflake.getLocation(), attackData.getLocationToFollow()));
    attackData.setRadius(BlackHoleStrategy.BLACKHOLE_RADIUS / 2 - BlackHoleStrategy.BLACKHOLE_RING_WIDTH * (1 - 1.5 * Math.random()));
    startTime = System.currentTimeMillis();
    //yellowish
    snowflake.setColor(new RGB(255, 201, 67));
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {
    getStrategy().stopAudio("blackhole.wav");
    getStrategy().stopAudio("blackhole-3.wav");
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    snowflake.setAlpha(((BlackHoleStrategy) getStrategy()).getAlpha());
    return Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), attackData.getRadius(), attackData.getAngle());
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 4;
  }

  @Override
  public boolean isFinished(Snowflake snowflake) {
    return (System.currentTimeMillis() - startTime) > 20000;
  }

}