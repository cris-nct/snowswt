package org.herbshouse.logic.snow.attack.impl.phase.parasites;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataParasites;

/**
 * Attack the mouse and snowflakes are moving like worms
 */
public class ParasitesPhase2 extends AbstractPhaseProcessor<AttackDataParasites> {

  protected ParasitesPhase2(AttackStrategy<AttackDataParasites> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataParasites attackData = getStrategy().getData(snowflake);
    attackData.setLocationToFollow(getStrategy().getFlagsConfiguration().getMouseLoc());
    attackData.setSpeedPhase1(Math.random() + 0.3);
    attackData.setCounter(0);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {

  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    Point2D locationToFollow = getStrategy().getData(snowflake).getLocationToFollow();
    double distToTarget = Utils.distance(snowflake.getLocation(), locationToFollow);
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(), locationToFollow);
    Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(),
        getStrategy().getData(snowflake).getSpeedPhase1(), directionToTarget);
    double func = Math.sin(distToTarget * 0.15);
    return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
  }

  @Override
  public int getCurrentPhaseIndex() {
    return 2;
  }

}
