package org.herbshouse.logic;

import org.herbshouse.controller.AbstractController;

public abstract class AbstractGenerator<T extends AbstractMovableObject>
    extends Thread implements GeneratorListener<T> {

  private AbstractController controller;

  public boolean isColliding(AbstractMovableObject obj1, AbstractMovableObject obj2) {
    int sumSize = (obj1.getSize() + obj2.getSize()) / 2 + 2;
    return Utils.distance(obj1.getLocation(), obj2.getLocation()) < sumSize;
  }

  public AbstractController getController() {
    return controller;
  }

  @Override
  public void setController(AbstractController controller) {
    this.controller = controller;
  }
}
