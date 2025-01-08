package org.herbshouse.logic.snow;

import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.snow.attack3.AttackData3;

public class Snowflake extends AbstractMovableObject {
    private final AttackData2 attackData2 = new AttackData2();
    private final AttackData3 attackData3 = new AttackData3();
    private final HappyWindSnowFlakeData happyWindData = new HappyWindSnowFlakeData();
    private final SnowTail snowTail;
    private boolean freezed = false;

    public Snowflake() {
        snowTail = new SnowTail(this);
    }

    public AttackData3 getAttackData3() {
        return attackData3;
    }

    public SnowTail getSnowTail() {
        return snowTail;
    }

    public AttackData2 getAttackData2() {
        return attackData2;
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