package org.herbshouse.logic.snow.attack.impl.phase.blackhole;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

public class BlackHolePhase2 extends AbstractPhaseProcessor<AttackDataBlackHole> {

  private long startTime = 0;

  protected BlackHolePhase2(AttackStrategy<AttackDataBlackHole> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataBlackHole attackData = getStrategy().getData(snowflake);
    attackData.setAngle(Utils.angleOfLine(snowflake.getLocation(), attackData.getLocationToFollow()));
    attackData.setLocationToFollow(new Point2D(99999, 99999));
    attackData.setRadius(BlackHoleStrategy.BLACKHOLE_RADIUS - BlackHoleStrategy.BLACKHOLE_RING_WIDTH * (1 - Math.random()));
    snowflake.setSize(10);
    snowflake.getSnowTail().setTailLength(50);
    snowflake.setShowTrail(true);
    snowflake.setShowHead(false);
    startTime = System.currentTimeMillis();
  }

  @Override
  public void endPhase() {

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
    return (System.currentTimeMillis() - startTime) > 15000;
  }

}