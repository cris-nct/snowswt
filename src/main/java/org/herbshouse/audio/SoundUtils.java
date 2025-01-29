package org.herbshouse.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoundUtils {

  private static final float SAMPLE_RATE = 44100;

  private SoundUtils() {

  }

  public static byte[] modulateTwoFrequencies(int durationSec, int step, int frequency1, int frequency2) {
    int countTotal = 4 * step * step;
    byte[] buffer = new byte[(int) (SAMPLE_RATE * durationSec)];
    int counter = 0;
    for (int i = frequency1 - step; i < frequency1 + step; i++) {
      for (int j = frequency2 - step; j < frequency2 + step; j++) {
        byte[] intermediateBuffer = modulateFrequencies(durationSec / countTotal, i, j);
        System.arraycopy(intermediateBuffer, 0, buffer, counter, intermediateBuffer.length);
        counter += intermediateBuffer.length;
      }
    }
    return buffer;
  }

  public static byte[] modulateFrequencies(int duration, int frequency1, int frequency2) {
    byte[] buffer = new byte[(int) (SAMPLE_RATE * duration * 2)]; // 2 channels
    for (int i = 0; i < buffer.length / 2; i++) {
      double angleLeft = 2.0 * Math.PI * i / (SAMPLE_RATE / frequency1);
      double angleRight = 2.0 * Math.PI * i / (SAMPLE_RATE / frequency2);
      buffer[2 * i] = (byte) (Math.sin(angleLeft) * 127); // Left channel
      buffer[2 * i + 1] = (byte) (Math.sin(angleRight) * 127); // Right channel
    }
    return buffer;
  }

  public static byte[] modulateFrequencies(int duration, int frequency) {
    byte[] buffer = new byte[(int) (SAMPLE_RATE * duration)];
    for (int i = 0; i < buffer.length; i++) {
      double angle = 2.0 * Math.PI * i / (SAMPLE_RATE / frequency);
      buffer[i] = (byte) (Math.sin(angle) * 63);
    }
    return buffer;
  }

  public static void playSound(byte[] buffer, float volume, LineListener listener) throws LineUnavailableException {
    AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, true);
    try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
      FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
      volumeControl.setValue(
          (volumeControl.getMaximum() - volumeControl.getMinimum()) * volume + volumeControl.getMinimum());
      line.open(format);
      if (listener != null) {
        line.addLineListener(listener);
      }
      line.start();
      line.write(buffer, 0, buffer.length);
      line.drain();
    }
  }

}
