package org.herbshouse.controller;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.herbshouse.audio.AudioPlayOrder;
import org.herbshouse.audio.AudioPlayType;
import org.herbshouse.audio.AudioPlayer;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.UserInfo;

public class DefaultLogicController implements LogicController {

  private final FlagsConfiguration flagsConfiguration = new FlagsConfiguration();
  private final List<GeneratorListener<? extends AbstractMovableObject>> listeners = new ArrayList<>();
  private int desiredFPS;
  private UserInfo userInfo;
  private Transform transform;
  private int currentAttackPhase;
  private AudioPlayer audioPlayer;

  @Override
  public void registerListener(GeneratorListener<?> listener) {
    listener.setLogicController(this);
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
    this.checkTriggerWind();
  }

  private void checkTriggerWind() {
    if (flagsConfiguration.isNormalWind() && !flagsConfiguration.isHappyWind()) {
      AudioPlayOrder order = new AudioPlayOrder("wind.wav");
      order.setType(AudioPlayType.BACKGROUND);
      order.setVolume(0.7f);
      this.audioPlayer.play(order);
    } else {
      this.audioPlayer.stop("wind.wav");
    }
  }

  @Override
  public void switchHappyWind() {
    if (!flagsConfiguration.isHappyWind()) {
      listeners.forEach(GeneratorListener::turnOnHappyWind);
    }
    flagsConfiguration.switchHappyWind();
    this.checkTriggerWind();
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
    if (flagsConfiguration.isEnemies()) {
      AudioPlayOrder order = new AudioPlayOrder("drum.wav");
      order.setType(AudioPlayType.BACKGROUND);
      this.audioPlayer.play(order);
    } else {
      this.audioPlayer.stop("drum.wav");
    }
  }

  @Override
  public void shutdown() {
    AudioPlayOrder order = new AudioPlayOrder("glass-breaking.wav");
    order.setCallback(() -> audioPlayer.shutdown());
    this.audioPlayer.play(order);
    listeners.forEach(GeneratorListener::shutdown);
  }

  @Override
  public void setAudio(AudioPlayer audioPlayer) {
    this.audioPlayer = audioPlayer;
  }

  public AudioPlayer getAudioPlayer() {
    return audioPlayer;
  }

  public Transform getTransform() {
    return transform;
  }

  public void setTransform(Transform transform) {
    this.transform = transform;
  }

  @Override
  public int getCurrentAttackPhase() {
    return currentAttackPhase;
  }

  public void setCurrentAttackPhase(int currentAttackPhase) {
    this.currentAttackPhase = currentAttackPhase;
  }

  public void setDesiredFPS(int desiredFPS) {
    this.desiredFPS = desiredFPS;
  }

  @Override
  public int getDesiredFps() {
    return desiredFPS;
  }

  @Override
  public void mouseMove(int x, int y) {
    Point2D mouseLoc = GuiUtils.toWorldCoord(convertLoc(x, y));
    getFlagsConfiguration().setMouseCurrentLocation(mouseLoc);
    getListeners().forEach(l -> l.mouseMove(mouseLoc));
  }

  @Override
  public void mouseDown(int button, int x, int y) {
    Point2D mouseLoc = GuiUtils.toWorldCoord(convertLoc(x, y));
    getListeners().forEach(l -> l.mouseDown(button, mouseLoc));
  }

  private Point convertLoc(int x, int y) {
    int locX = x;
    int locY = y;
    if (getFlagsConfiguration().isFlipImage()) {
      float[] data = {locX, locY};
      getTransform().transform(data);
      locX = (int) data[0];
      locY = (int) data[1];
    }
    return new Point(locX, locY);
  }

}
