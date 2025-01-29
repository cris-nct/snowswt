package org.herbshouse.logic.graphicalSounds;

import java.util.Collections;
import java.util.List;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.controller.GraphicalSoundConfig;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.graphicalSounds.updaters.AbstractGraphicalSoundUpdater;
import org.herbshouse.logic.graphicalSounds.updaters.GraphicalSoundUpdaterMultiRow;
import org.herbshouse.logic.graphicalSounds.updaters.GraphicalSoundUpdaterSingleRow;

public class GraphicalSoundsGenerator extends AbstractGenerator<GraphicalSound> {

  private GraphicalSound sound;

  private boolean shutdown;

  private AbstractGraphicalSoundUpdater updater;

  private boolean slowPlay;

  @Override
  public void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    super.init(flagsConfiguration, screenBounds);
    this.selectUpdater();
  }

  private void generateSound(GraphicalSoundConfig config) {
    sound = new GraphicalSound(config.getDuration(), config.getFrequency1(), config.getFrequency2());
    sound.setSize(150);
    sound.setLocation(new Point2D(0, screenBounds.height / 2.0));
    sound.setColor(new RGB(255, 255, 0));
    sound.setSpeed(1);
  }

  @Override
  public void run() {
    while (!shutdown) {
      if (config.isGraphicalSounds() && !getFlagsConfiguration().isPause()) {
        if (sound != null && !updater.update(sound)) {
          sound = null;
        }
        if (sound == null) {
          Utils.sleep(1000);
        } else if (slowPlay) {
          Utils.sleep(0, 1000);
        }
      } else {
        Utils.sleep(getSleepDurationDoingNothing());
      }
    }
  }

  @Override
  protected int getSleepDuration() {
    return 0;
  }

  @Override
  public void turnOnHappyWind() {

  }

  @Override
  public void switchDebug() {

  }

  @Override
  public void mouseMove(Point2D mouseLocation) {

  }

  @Override
  public void mouseDown(int button, Point2D mouseLocation) {

  }

  @Override
  public void mouseScrolled(int count) {

  }

  @Override
  public void reset() {

  }

  @Override
  public List<GraphicalSound> getMoveableObjects() {
    return sound == null ? Collections.emptyList() : List.of(sound);
  }

  @Override
  public void shutdown() {
    shutdown = true;
  }

  @Override
  public void provideImageData(ImageData imageData) {

  }

  @Override
  public void switchAttack() {

  }

  @Override
  public void changeAttackType(int oldAttackType, int newAttackType) {

  }

  @Override
  public void switchBlackHoles() {

  }

  @Override
  public void switchIndividualMovements() {

  }

  @Override
  public boolean canControllerStart() {
    return true;
  }

  @Override
  public void changeGraphicalSound(GraphicalSoundConfig graphicalSoundConfig) {
    generateSound(graphicalSoundConfig);
    this.slowPlay = graphicalSoundConfig.isSlowPlay();
    this.selectUpdater();
  }

  @Override
  public void switchGraphicalSounds() {
    if (!getFlagsConfiguration().isGraphicalSounds()) {
      if (sound != null) {
        sound.cleanup();
      }
    }
  }

  private void selectUpdater() {
    this.updater = getFlagsConfiguration().getGraphicalSoundConfig().isMultiRowsRendering() ?
        new GraphicalSoundUpdaterMultiRow(screenBounds) : new GraphicalSoundUpdaterSingleRow(screenBounds);
  }

}
