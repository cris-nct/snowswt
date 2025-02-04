package org.herbshouse.logic.blackhole;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

public class BlackHolePhase2WhiteRing extends AbstractPhaseProcessor<AttackDataBlackHole> {

  private long startTime = 0;

  protected BlackHolePhase2WhiteRing(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    attackData.setAngle(Utils.angleOfLine(snowflake.getLocation(), getStrategy().getFlagsConfiguration().getMouseLoc()));
    attackData.setLocationToFollow(new Point2D(99999, 99999));
    attackData.setRadius(BlackHoleStrategy.BLACKHOLE_RADIUS - BlackHoleStrategy.BLACKHOLE_RING_WIDTH * (1 - Math.random()));
    snowflake.setShowTrail(true);
    snowflake.getSnowTail().setTailLength(50);
    snowflake.setShowHead(false);
    snowflake.setColor(new RGB(255, 255, 255));
    startTime = System.currentTimeMillis();
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {

  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    snowflake.setAlpha(((BlackHoleStrategy) getStrategy()).getAlpha());
    return Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), attackData.getRadius(), attackData.getAngle());
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 2;
  }

  @Override
  public boolean isFinished(Snowflake snowflake) {
    return (System.currentTimeMillis() - startTime) > 10000;
  }

}