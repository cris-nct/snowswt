package org.herbshouse.audio;

public class DefaultAudioPlayer implements AudioPlayer {

  private Thread playingThread;

  @Override
  public void play(AudioPlayOrder order) {
    playingThread = new AudioPlayerThread(order);
    playingThread.start();
  }

  @Override
  public void stop() {
    if (isPlaying()) {
      playingThread.interrupt();
    }
    playingThread = null;
  }

  @Override
  public boolean isPlaying() {
    return playingThread != null && playingThread.isAlive();
  }

}
