package org.herbshouse.audio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultAudioPlayer implements AudioPlayer {

  private final List<AudioPlayerThread> playingThreads = new CopyOnWriteArrayList<>();

  @Override
  public void play(AudioPlayOrder order) {
    cleanup();
    AudioPlayerThread playingThread = new AudioPlayerThread(order);
    playingThread.start();
    playingThreads.add(playingThread);
  }

  @Override
  public void stop(String filename) {
    for (AudioPlayerThread playingThread : playingThreads) {
      if (filename.equals(playingThread.getOrder().getFilename())) {
        playingThread.interrupt();
      }
    }
  }

  @Override
  public boolean isPlaying(String filename) {
    return playingThreads.stream()
        .anyMatch(t -> t.getOrder().getFilename().equals(filename) && t.isPlaying());
  }

  @Override
  public void shutdown() {
    for (AudioPlayerThread playingThread : playingThreads) {
      playingThread.interrupt();
    }
    playingThreads.clear();
  }

  private void cleanup() {
    // Remove non-playing threads directly
    playingThreads.removeIf(thread -> !thread.isPlaying());
  }
}