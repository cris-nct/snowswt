package org.herbshouse.controller;

public interface SnowflakesController {

  void switchNormalWind();

  void switchHappyWind();

  void switchBigBalls();

  void switchDebug();

  void switchObjectsTail();

  void switchAttack();

  void setAttackType(int type);

  void switchMercedesSnowflakes();

  void increaseSnowLevel();

  void decreaseSnowLevel();

  void switchIndividualMovements();

  int getCurrentAttackPhase();
}
