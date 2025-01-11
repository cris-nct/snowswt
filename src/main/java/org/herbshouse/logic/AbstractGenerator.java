package org.herbshouse.logic;

import org.herbshouse.controller.DefaultLogicController;
import org.herbshouse.controller.ViewController;

public abstract class AbstractGenerator<T extends AbstractMovableObject>
    extends Thread implements GeneratorListener<T>, ViewController {

  private ViewController viewController;

  private DefaultLogicController logicController;

  public DefaultLogicController getLogicController() {
    return logicController;
  }

  @Override
  public void setLogicController(DefaultLogicController logicController) {
    this.logicController = logicController;
  }

  protected abstract int getSleepDuration();

  protected int getSleepDurationDoingNothing() {
    return 100;
  }

  public void setViewController(ViewController viewController) {
    this.viewController = viewController;
  }

  public void substractAreaFromShell(int[] polygon) {
    viewController.substractAreaFromShell(polygon);
  }

  public void resetScreenSurface() {
    viewController.resetScreenSurface();
  }

}
