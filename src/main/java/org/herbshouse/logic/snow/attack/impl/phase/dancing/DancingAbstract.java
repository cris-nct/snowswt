package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackDataDancing;

abstract class DancingAbstract extends AbstractPhaseProcessor<AttackDataDancing> {

  protected DancingAbstract(AttackStrategy<AttackDataDancing> strategy) {
    super(strategy);
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfLine(snowflake.getLocation(),
        getStrategy().getData(snowflake).getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
  }

}
