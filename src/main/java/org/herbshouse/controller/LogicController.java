package org.herbshouse.controller;

import java.util.List;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.UserInfo;

public interface LogicController {

  void mouseMove(int x, int y);

  void mouseDown(int button, int x, int y);

  void mouseScrolled(int count);

  void switchNormalWind();

  void switchHappyWind();

  void flipImage();

  void pause();

  UserInfo getUserInfo();

  FlagsConfiguration getFlagsConfiguration();

  void registerListener(GeneratorListener<?> listener);

  List<GeneratorListener<? extends AbstractMovableObject>> getListeners();

  int getDesiredFps();

  void reset();

  void switchBigBalls();

  void switchDebug();

  void switchObjectsTail();

  void switchAttack();

  void setAttackType(int type);

  void switchMercedesSnowflakes();

  void increaseSnowLevel();

  void decreaseSnowLevel();

  void switchYoutube();

  void switchEnemies();

  int getCurrentAttackPhase();

  void shutdown();

}
