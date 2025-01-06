package org.herbshouse.logic.snow;

import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;

public class Snowflake extends AbstractMovableObject {
    private final AttackData attackData = new AttackData();
    private final HappyWindSnowFlakeData happyWindData = new HappyWindSnowFlakeData();
    private final SnowTail snowTail;
    private boolean freezed = false;

    public Snowflake() {
        snowTail = new SnowTail(this);
    }

    public SnowTail getSnowTail() {
        return snowTail;
    }

    public AttackData getAttackData() {
        return attackData;
    }

    public HappyWindSnowFlakeData getHappyWindData() {
        return happyWindData;
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