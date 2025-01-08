package org.herbshouse.logic.snow.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

public class Phase0 extends AbstractPhaseProcessor {

    protected Phase0(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        super(flagsConfiguration, screenBounds);
    }

    @Override
    protected void prepareNextPhase(AttackData3 attackData3) {
        if (attackData3.getLocationToFollow() == null) {
            Point2D dest = Utils.moveToDirection(getFlagsConfiguration().getMouseLoc(), 600, Math.toRadians(Math.random() * 360));
            attackData3.setLocationToFollow(dest);
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
