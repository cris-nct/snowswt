package org.herbshouse.logic.snow.attack3;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;

public abstract class AbstractPhaseProcessor implements IAttack3PhaseProcessor {
    private final FlagsConfiguration flagsConfiguration;
    private final Rectangle screenBounds;
    private AbstractPhaseProcessor nextPhase;

    protected AbstractPhaseProcessor(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        this.flagsConfiguration = flagsConfiguration;
        this.screenBounds = screenBounds;
    }

    @Override
    public final void initNextPhase(Snowflake snowflake) {
        this.prepareNextPhase(snowflake.getAttackData3());
        snowflake.getAttackData3().setPhase(getNextPhaseProcessor().getCurrentPhaseIndex());
    }

    protected abstract void prepareNextPhase(AttackData3 attackData3);

    protected FlagsConfiguration getFlagsConfiguration() {
        return flagsConfiguration;
    }

    protected Rectangle getScreenBounds() {
        return screenBounds;
    }

    public void setNextPhase(AbstractPhaseProcessor nextPhase) {
        this.nextPhase = nextPhase;
    }

    @Override
    public AbstractPhaseProcessor getNextPhaseProcessor() {
        return nextPhase;
    }
}
