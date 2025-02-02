package org.herbshouse.controller;

public class GraphicalSoundConfig {

  private final int frequency1;

  private final int frequency2;

  private final int duration;

  private final int speed;

  private final boolean multiRowsRendering;

  private final int circularSoundLevel;

  public GraphicalSoundConfig(
      int frequency1,
      int frequency2,
      int duration,
      int speed,
      boolean multiRowsRendering,
      int circularSoundLevel
  ) {
    this.frequency1 = frequency1;
    this.frequency2 = frequency2;
    this.duration = duration;
    this.speed = speed;
    this.multiRowsRendering = multiRowsRendering;
    this.circularSoundLevel = circularSoundLevel;
  }

  public int getCircularSoundLevel() {
    return circularSoundLevel;
  }

  public boolean isMultiRowsRendering() {
    return multiRowsRendering;
  }

  public int getFrequency1() {
    return frequency1;
  }

  public int getFrequency2() {
    return frequency2;
  }

  public int getDuration() {
    return duration;
  }

  public int getSpeed() {
    return speed;
  }
}
