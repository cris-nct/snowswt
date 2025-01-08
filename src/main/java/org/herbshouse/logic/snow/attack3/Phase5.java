package org.herbshouse.logic.snow.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

public class Phase5 extends AbstractPhaseProcessor {
    protected Phase5(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        super(flagsConfiguration, screenBounds);
    }

    @Override
    protected void prepareNextPhase(AttackData3 attackData3) {
        attackData3.setLocationToFollow(null);
    }

    @Override
    public Point2D computeLocation(Snowflake snowflake) {
        AttackData3 attackData3 = snowflake.getAttackData3();
        double distToTarget = Utils.distance(snowflake.getLocation(), attackData3.getLocationToFollow());
        double directionToTarget = Utils.angleOfPath(snowflake.getLocation(), attackData3.getLocationToFollow());
        Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), attackData3.getSpeedPhase1(), directionToTarget);
        double func = Math.sin(distToTarget * 0.030) + 1 / distToTarget;
        if (attackData3.getCounter() % 200 == 0) {
            attackData3.setLocationToFollow(new Point2D(getScreenBounds().width * Math.random(), getScreenBounds().height * Math.random()));
        }
        if (attackData3.getCounter() > 10000) {
            attackData3.setLocationToFollow(getFlagsConfiguration().getMouseLoc());
        }
        return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
    }

    @Override
    public int getCurrentPhaseIndex() {
        return 5;
    }

}
