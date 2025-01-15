package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.AbstractPhaseProcessor;
import org.herbshouse.logic.snow.data.AttackData2;

abstract class AbstractA2 extends AbstractPhaseProcessor<AttackData2> {

  protected AbstractA2(AttackStrategy<AttackData2> strategy) {
    super(strategy);
  }

  @Override
  public Point2D computeLocation(Snowflake snowflake) {
    double directionToTarget = Utils.angleOfPath(snowflake.getLocation(),
        getStrategy().getData(snowflake).getLocationToFollow());
    return Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
  }

}
