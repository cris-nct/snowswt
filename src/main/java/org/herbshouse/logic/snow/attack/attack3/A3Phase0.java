package org.herbshouse.logic.snow.attack.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseProcessor;

public class A3Phase0 extends AbstractPhaseProcessor<AttackData3> {

    protected A3Phase0(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        super(flagsConfiguration, screenBounds);
    }

    @Override
    public AttackData3 getData(Snowflake snowflake) {
        return snowflake.getAttackData3();
    }

    @Override
    protected void prepareNextPhase(Snowflake snowflake) {
        AttackData3 attackData = this.getData(snowflake);
        if (attackData.getLocationToFollow() == null) {
            Point2D dest = Utils.moveToDirection(getFlagsConfiguration().getMouseLoc(), 600, Math.toRadians(Math.random() * 360));
            attackData.setLocationToFollow(dest);
        }
    }

    @Override
    public Point2D computeLocation(Snowflake snowflake) {
        return snowflake.getLocation();
    }

    @Override
    public int getCurrentPhaseIndex() {
        return 0;
    }

}
