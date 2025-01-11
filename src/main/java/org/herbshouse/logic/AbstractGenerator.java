package org.herbshouse.logic;

import org.herbshouse.controller.AbstractController;

public abstract class AbstractGenerator<T extends AbstractMovableObject>
    extends Thread implements GeneratorListener<T> {

  private AbstractController controller;

  public AbstractController getController() {
    return controller;
  }

  @Override
  public void setController(AbstractController controller) {
    this.controller = controller;
  }

  protected abstract int getSleepDuration();

  protected int getSleepDurationDoingNothing() {
    return 100;
  }

}
