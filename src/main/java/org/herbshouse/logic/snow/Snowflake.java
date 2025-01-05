package org.herbshouse.logic.snow;

import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snowflake extends AbstractMovableObject {
    private final List<Point2D> historyLocations = new CopyOnWriteArrayList<>();
    private final AttackData attackData = new AttackData();
    private final HappyWindSnowFlakeData happyWindData = new HappyWindSnowFlakeData();
    private boolean freezed = false;

    public List<Point2D> getHistoryLocations() {
        return Collections.unmodifiableList(historyLocations);
    }

    public AttackData getAttackData() {
        return attackData;
    }

    public HappyWindSnowFlakeData getHappyWindData() {
        return happyWindData;
    }

    public void registerHistoryLocation() {
        if (getLocation() != null) {
            historyLocations.add(new Point2D(getLocation())); // Assuming Point2D has a copy constructor
        }
    }

    public void freeze() {
        if (!freezed) {
            freezed = true;
            setColor(GuiUtils.FREEZE_COLOR);
        }
    }

    public boolean isFreezed() {
        return freezed;
    }
}