package org.herbshouse.controller;

import java.util.ArrayList;
import java.util.List;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.UserInfo;

public abstract class AbstractController implements Controller {

  private final FlagsConfiguration flagsConfiguration = new FlagsConfiguration();

  private final List<GeneratorListener<? extends AbstractMovableObject>> listeners = new ArrayList<>();

  private UserInfo userInfo;

  @Override
  public void registerListener(GeneratorListener<?> listener) {
    listener.setController(this);
    listener.init(flagsConfiguration, GuiUtils.SCREEN_BOUNDS);
    listeners.add(listener);
  }

  @Override
  public List<GeneratorListener<? extends AbstractMovableObject>> getListeners() {
    return listeners;
  }

  @Override
  public void mouseScrolled(int count) {
    getListeners().forEach(l -> l.mouseScrolled(count));
  }

  @Override
  public void flipImage() {
    flagsConfiguration.switchFlipImage();
  }

  public FlagsConfiguration getFlagsConfiguration() {
    return flagsConfiguration;
  }

  @Override
  public UserInfo getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  @Override
  public void switchNormalWind() {
    flagsConfiguration.switchNormalWind();
  }

  @Override
  public void switchHappyWind() {
    if (!flagsConfiguration.isHappyWind()) {
      listeners.forEach(GeneratorListener::turnOnHappyWind);
    }
    flagsConfiguration.switchHappyWind();
  }

  @Override
  public void pause() {
    flagsConfiguration.switchFreezeSnowflakes();
    listeners.forEach(GeneratorListener::freezeMovableObjects);
  }

  @Override
  public void reset() {
    listeners.forEach(GeneratorListener::reset);
  }

  @Override
  public void switchBigBalls() {
    flagsConfiguration.switchBigBalls();
  }

  @Override
  public void switchDebug() {
    flagsConfiguration.switchDebug();
    listeners.forEach(GeneratorListener::switchDebug);
  }

  @Override
  public void switchObjectsTail() {
    flagsConfiguration.switchObjectsTail();
  }

  @Override
  public void switchAttack() {
    flagsConfiguration.switchAttack();
    beforeAttack();
    listeners.forEach(GeneratorListener::switchAttack);
  }

  private void beforeAttack() {
    if (flagsConfiguration.isAttack()
        && flagsConfiguration.getAttackType() == 4
        && !flagsConfiguration.isObjectsTail()) {
      flagsConfiguration.switchObjectsTail();
    } else if (!flagsConfiguration.isAttack() && flagsConfiguration.isObjectsTail()) {
      flagsConfiguration.switchObjectsTail();
    } else if (flagsConfiguration.getAttackType() == 3 && !flagsConfiguration.isObjectsTail()) {
      getFlagsConfiguration().switchObjectsTail();
    }
  }

  @Override
  public void setAttackType(int type) {
    flagsConfiguration.setAttackType(type);
    beforeAttack();
    if (flagsConfiguration.isAttack()) {
      listeners.forEach(GeneratorListener::changeAttackType);
    }
  }

  @Override
  public void switchMercedesSnowflakes() {
    flagsConfiguration.switchMercedesSnowflakes();
  }

  @Override
  public void increaseSnowLevel() {
    if (flagsConfiguration.getSnowingLevel() < 10) {
      flagsConfiguration.increaseSnowingLevel();
      listeners.forEach(GeneratorListener::changedSnowingLevel);
    }
  }

  @Override
  public void decreaseSnowLevel() {
    if (flagsConfiguration.getSnowingLevel() > 0) {
      flagsConfiguration.decreaseSnowingLevel();
      listeners.forEach(GeneratorListener::changedSnowingLevel);
    }
  }

  @Override
  public void switchYoutube() {
    flagsConfiguration.switchYoutube();
  }

  @Override
  public void switchEnemies() {
    flagsConfiguration.switchEnemies();
  }

  @Override
  public void shutdown() {
    listeners.forEach(GeneratorListener::shutdown);
  }

}
