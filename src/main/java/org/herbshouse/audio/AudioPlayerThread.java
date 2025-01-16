package org.herbshouse.audio;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.Utils;

class AudioPlayerThread extends Thread {

  private final AudioPlayOrder order;

  private Clip clip;

  public AudioPlayerThread(AudioPlayOrder order) {
    this.order = order;
    this.setName("AudioFilePlayer" + System.currentTimeMillis());
  }

  @Override
  public void run() {
    try (
        InputStream is = SnowingApplication.class.getClassLoader()
            .getResourceAsStream(order.getFilename())) {
      //noinspection DataFlowIssue
      try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(is)) {
        clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
        if (order.getMilliseconds() == -1) {
          Utils.sleep((int) (clip.getMicrosecondLength() / 1000));
        } else {
          Utils.sleep(order.getMilliseconds());
        }
        clip.stop();
      }
    } catch (
        UnsupportedAudioFileException e) {
      System.err.println("The specified audio file is not supported: " + e.getMessage());
    } catch (
        IOException e) {
      System.err.println("An I/O error occurred: " + e.getMessage());
    } catch (
        LineUnavailableException e) {
      System.err.println("Audio line for playing back is unavailable: " + e.getMessage());
    }
  }

  @Override
  public void interrupt() {
    super.interrupt();
    if (clip != null && clip.isRunning()) {
      clip.stop();
    }
  }

}