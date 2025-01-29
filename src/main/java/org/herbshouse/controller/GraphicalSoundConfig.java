package org.herbshouse.controller;

public class GraphicalSoundConfig {

  private final int frequency1;

  private final int frequency2;

  private final int duration;

  private final boolean slowPlay;

  private final boolean multiRowsRendering;

  public GraphicalSoundConfig(int frequency1, int frequency2, int duration, boolean slowPlay, boolean multiRowsRendering) {
    this.frequency1 = frequency1;
    this.frequency2 = frequency2;
    this.duration = duration;
    this.slowPlay = slowPlay;
    this.multiRowsRendering = multiRowsRendering;
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

  public boolean isSlowPlay() {
    return slowPlay;
  }
}
