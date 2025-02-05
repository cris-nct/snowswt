package org.herbshouse.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.Utils;

class AudioPlayerThread extends Thread {

  private final AudioPlayOrder order;

  private Clip clip;

  public AudioPlayerThread(AudioPlayOrder order) {
    this.order = order;
    this.setName("AudioFilePlayer" + order.getFilename() + "-" + System.currentTimeMillis());
  }

  public AudioPlayOrder getOrder() {
    return order;
  }

  @Override
  public void run() {
    if (this.isLoopContinuous() && order.getCallback() != null) {
      throw new IllegalArgumentException("Cannot use callback with continuous loop");
    }
    //noinspection DataFlowIssue
    try (
        InputStream is = new BufferedInputStream(SnowingApplication.class.getClassLoader()
            .getResourceAsStream(order.getFilename()))) {
      try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(is)) {
        clip = initAudio(audioIn);
        if (this.isLoopContinuous()) {
          clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
          clip.start();
          if (order.getMilliseconds() == -1) {
            Utils.sleep((int) (clip.getMicrosecondLength() / 1000));
          } else {
            Utils.sleep(order.getMilliseconds());
          }
          stopAudio();
          if (order.getCallback() != null) {
            order.getCallback().run();
          }
        }
      }
    } catch (
        UnsupportedAudioFileException e) {
      System.err.println("The specified audio file is not supported: " + e.getMessage());
      e.printStackTrace();
    } catch (
        IOException e) {
      System.err.println("An I/O error occurred: " + e.getMessage());
      e.printStackTrace();
    } catch (
        LineUnavailableException e) {
      System.err.println("Audio line for playing back is unavailable: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private Clip initAudio(AudioInputStream audioIn) throws LineUnavailableException, IOException {
    Clip clip = AudioSystem.getClip();
    clip.open(audioIn);
    FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    float min = volumeControl.getMinimum();
    float max = volumeControl.getMaximum();
    float range = max - min;
    float gain = (range * order.getVolume()) + min;
    volumeControl.setValue(gain);
    return clip;
  }

  private boolean isLoopContinuous() {
    return (order.getType() == AudioPlayType.BACKGROUND || order.getType() == AudioPlayType.EFFECT_LOOP);
  }

  @Override
  public void interrupt() {
    stopAudio();
    super.interrupt();
  }

  private void stopAudio() {
    if (clip != null) {
      if (clip.isRunning()) {
        clip.stop();
      }
      clip.close();
      clip = null;
    }
  }

  public boolean isPlaying() {
    return this.isAlive() || clip != null;
  }

}