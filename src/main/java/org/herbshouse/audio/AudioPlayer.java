package org.herbshouse.audio;

public interface AudioPlayer {

  void play(AudioPlayOrder order);

  void stop();

  boolean isPlaying();
}
