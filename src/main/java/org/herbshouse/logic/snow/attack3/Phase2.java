package org.herbshouse.logic.snow.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

public class Phase2 extends AbstractPhaseProcessor {

    protected Phase2(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        super(flagsConfiguration, screenBounds);
    }

    @Override
    public Point2D computeLocation(Snowflake snowflake) {
        Point2D locationToFollow = snowflake.getAttackData3().getLocationToFollow();
        double distToTarget = Utils.distance(snowflake.getLocation(), locationToFollow);
        double directionToTarget = Utils.angleOfPath(snowflake.getLocation(), locationToFollow);
        Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), snowflake.getAttackData3().getSpeedPhase1(), directionToTarget);
        double func = Math.sin(distToTarget * 0.15);
        return Utils.moveToDirection(newLoc, func, directionToTarget + Math.PI / 2);
    }

    @Override
    protected void prepareNextPhase(AttackData3 attackData3) {
        attackData3.setSpeedPhase1(Math.random() / 2 + 0.3);
        attackData3.setLocationToFollow(getFlagsConfiguration().getMouseLoc());
    }

    @Override
    public int getCurrentPhaseIndex() {
        return 2;
    }

}
