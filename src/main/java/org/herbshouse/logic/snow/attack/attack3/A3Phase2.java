package org.herbshouse.logic.snow.attack.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseProcessor;

public class A3Phase2 extends AbstractPhaseProcessor<AttackData3> {

    protected A3Phase2(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
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
    protected void prepareNextPhase(Snowflake snowflake) {
        AttackData3 attackData = this.getData(snowflake);
        attackData.setSpeedPhase1(Math.random() / 2 + 0.3);
        attackData.setLocationToFollow(getFlagsConfiguration().getMouseLoc());
    }

    @Override
    public AttackData3 getData(Snowflake snowflake) {
        return snowflake.getAttackData3();
    }

    @Override
    public int getCurrentPhaseIndex() {
        return 2;
    }

}
