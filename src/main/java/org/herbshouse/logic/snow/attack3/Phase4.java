package org.herbshouse.logic.snow.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

public class Phase4 extends AbstractPhaseProcessor {
    protected Phase4(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        super(flagsConfiguration, screenBounds);
    }

    @Override
    protected void prepareNextPhase(AttackData3 attackData3) {
        attackData3.setSpeedPhase1(1);
        attackData3.setLocationToFollow(getFlagsConfiguration().getMouseLoc());
    }

    @Override
    public Point2D computeLocation(Snowflake snowflake) {
        AttackData3 attackData3 = snowflake.getAttackData3();
        double distToTarget = Utils.distance(snowflake.getLocation(), attackData3.getLocationToFollow());
        double directionToTarget = Utils.angleOfPath(snowflake.getLocation(), attackData3.getLocationToFollow());
        Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), attackData3.getSpeedPhase1(), directionToTarget);
        double func = Math.tanh(distToTarget * 0.5) * 5 / distToTarget;
        return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
    }

    @Override
    public int getCurrentPhaseIndex() {
        return 4;
    }

}
