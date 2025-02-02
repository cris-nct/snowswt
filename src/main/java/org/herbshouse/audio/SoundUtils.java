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
        byte[] intermediateBuffer = modulateFrequencies((double) durationSec / countTotal / 2, i, j, 1, 1);
        System.arraycopy(intermediateBuffer, 0, buffer, counter, intermediateBuffer.length);
        counter += intermediateBuffer.length;
      }
    }
    return buffer;
  }

  public static byte[] circularSound(int durationSec, float step, int frequency1, int frequency2) {
    byte[] buffer = new byte[(int) (SAMPLE_RATE * durationSec * 4)]; // 2 channels, 2 bytes per sample

    float maxVolume = 1.0f;
    float leftVolume = maxVolume;
    float dirLeft = -1;

    float minVolume = 0.0f;
    float rightVolume = minVolume;
    float dirRight = 1;

    for (int i = 0; i < buffer.length / 4; i++) {
      // Left channel
      double angleLeft = 2.0 * Math.PI * frequency1 * i / SAMPLE_RATE;
      double sampleLeft = leftVolume * Math.sin(angleLeft);
      short valueLeft = (short) (sampleLeft * Short.MAX_VALUE);

      // Right channel
      double angleRight = 2.0 * Math.PI * frequency2 * i / SAMPLE_RATE;
      double sampleRight = rightVolume * Math.sin(angleRight);
      short valueRight = (short) (sampleRight * Short.MAX_VALUE);

      buffer[4 * i] = (byte) (valueLeft & 0xFF);
      buffer[4 * i + 1] = (byte) ((valueLeft >> 8) & 0xFF);
      buffer[4 * i + 2] = (byte) (valueRight & 0xFF);
      buffer[4 * i + 3] = (byte) ((valueRight >> 8) & 0xFF);

      // Update volumes
      if (leftVolume < minVolume || leftVolume > maxVolume) {
        dirLeft = -dirLeft;
      }
      if (rightVolume < minVolume || rightVolume > maxVolume) {
        dirRight = -dirRight;
      }
      leftVolume += dirLeft * step;
      rightVolume += dirRight * step;
    }
    return buffer;
  }

  public static byte[] modulateFrequencies(double duration, int frequency1, int frequency2, float leftVolume, float rightVolume) {
    byte[] buffer = new byte[(int) (SAMPLE_RATE * duration * 4)];
    for (int i = 0; i < buffer.length / 4; i++) {
      // Left channel
      double angleLeft = 2.0 * Math.PI * frequency1 * i / SAMPLE_RATE;
      double sampleLeft = leftVolume * Math.sin(angleLeft);
      short valueLeft = (short) (sampleLeft * Short.MAX_VALUE);

      // Right channel (can be the same or different)
      double angleRight = 2.0 * Math.PI * frequency2 * i / SAMPLE_RATE;
      double sampleRight = rightVolume * Math.sin(angleRight);
      short valueRight = (short) (sampleRight * Short.MAX_VALUE);

      buffer[4 * i] = (byte) (valueLeft & 0xFF);
      buffer[4 * i + 1] = (byte) ((valueLeft >> 8) & 0xFF);
      buffer[4 * i + 2] = (byte) (valueRight & 0xFF);
      buffer[4 * i + 3] = (byte) ((valueRight >> 8) & 0xFF);
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
