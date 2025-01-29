package org.herbshouse.logic.graphicalSounds.updaters;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.logic.graphicalSounds.GraphicalSound;

public abstract class AbstractGraphicalSoundUpdater {

  protected final Rectangle screenBounds;

  public AbstractGraphicalSoundUpdater(Rectangle screenBounds) {
    this.screenBounds = screenBounds;
  }

  public abstract boolean update(GraphicalSound sound);

}
