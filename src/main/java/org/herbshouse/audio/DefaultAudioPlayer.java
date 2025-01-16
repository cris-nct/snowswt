package org.herbshouse.audio;

import java.util.ArrayList;
import java.util.List;

public class DefaultAudioPlayer implements AudioPlayer {

  private final List<AudioPlayerThread> playingThreads = new ArrayList<>();

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
        if (playingThread.isAlive()) {
          playingThread.interrupt();
        } else {
          playingThread.stopAudio();
        }
      }
    }
  }

  @Override
  public boolean isPlaying(String filename) {
    return playingThreads.stream()
        .anyMatch(t -> t.getOrder().getFilename().equals(filename) && t.isPlaying());
  }

  private void cleanup() {
    List<AudioPlayerThread> newList = playingThreads.stream().filter(AudioPlayerThread::isPlaying)
        .toList();
    playingThreads.clear();
    playingThreads.addAll(newList);
  }

}
