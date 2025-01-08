package org.herbshouse.logic.snow.attack;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;

public abstract class AbstractPhaseProcessor<T extends AbstractAttackData> implements IAttackPhaseProcessor<T> {
    private final FlagsConfiguration flagsConfiguration;
    private final Rectangle screenBounds;
    private AbstractPhaseProcessor<T> nextPhase;

    protected AbstractPhaseProcessor(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        this.flagsConfiguration = flagsConfiguration;
        this.screenBounds = screenBounds;
    }

    @Override
    public final void initNextPhase(Snowflake snowflake) {
        this.prepareNextPhase(snowflake);
        if (getNextPhaseProcessor() != null) {
            this.getData(snowflake).setPhase(getNextPhaseProcessor().getCurrentPhaseIndex());
        }
    }

    protected abstract void prepareNextPhase(Snowflake snowflake);

    protected FlagsConfiguration getFlagsConfiguration() {
        return flagsConfiguration;
    }

    protected Rectangle getScreenBounds() {
        return screenBounds;
    }

    public void setNextPhase(AbstractPhaseProcessor<T> nextPhase) {
        this.nextPhase = nextPhase;
    }

    @Override
    public AbstractPhaseProcessor<T> getNextPhaseProcessor() {
        return nextPhase;
    }
}
