package org.herbshouse.logic.snow.attack.phase;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.snow.attack.data.AbstractAttackData;

public abstract class AbstractPhaseProcessor<T extends AbstractAttackData> implements
    AttackPhaseProcessor<T> {

  private final FlagsConfiguration flagsConfiguration;
  private final Rectangle screenBounds;
  private AbstractPhaseProcessor<T> nextPhase;

  protected AbstractPhaseProcessor(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;
  }

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
