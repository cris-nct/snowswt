package org.herbshouse.logic;

import java.util.List;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.DefaultLogicController;
import org.herbshouse.controller.FlagsConfiguration;

public interface GeneratorListener<T extends AbstractMovableObject> {

  void turnOnHappyWind();

  void freezeMovableObjects();

  void switchDebug();

  void mouseMove(Point2D mouseLocation);

  void mouseDown(int button, Point2D mouseLocation);

  void mouseScrolled(int count);

  void reset();

  void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds);

  default int getCountdown() {
    return -1;
  }

  List<T> getMoveableObjects();

  void shutdown();

  void provideImageData(ImageData imageData);

  void switchAttack();

  default void changedSnowingLevel() {
  }

  default void changedFractalType() {

  }

  void changeAttackType();

  void setLogicController(DefaultLogicController logicController);

  boolean isForceRedraw();

  void setForceRedraw(boolean redraw);

  void switchBlackHoles();

  void switchIndividualMovements();

  boolean canControllerStart();
}
