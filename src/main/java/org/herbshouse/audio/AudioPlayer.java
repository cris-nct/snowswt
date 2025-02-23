package org.herbshouse.audio;

public interface AudioPlayer {

  void play(AudioPlayOrder order);

  void stop(String filename);

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean isPlaying(String filename);

  void shutdown();

}
