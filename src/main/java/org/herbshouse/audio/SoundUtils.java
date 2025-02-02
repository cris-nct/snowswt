package org.herbshouse.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.herbshouse.controller.GraphicalSoundConfig;
import org.herbshouse.logic.Utils;

public final class SoundUtils {

  public static final float SAMPLE_RATE = 44100;
  public static final boolean SIGNED = true;
  public static final boolean BIG_ENDIAN = false;
  public static final int SAMPLE_SIZE_BYTES = 16;

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

  public static byte[] modulateFrequencies(GraphicalSoundConfig config) {
    byte[] buffer = new byte[(int) (SAMPLE_RATE * config.getDuration() * config.getChannels() * 2)]; // 2 channels, 2 bytes per sample
    boolean circularVolume = (config.getCircularSoundLevel() > 1);
    float minVolume = 0.0f;
    float maxVolume = 1.0f;
    float leftVolume = circularVolume ? maxVolume : 1.0f;
    float dirLeft = -1;
    float rightVolume = circularVolume ? minVolume : 1.0f;
    float dirRight = 1;
    float step = (float) Utils.linearInterpolation(config.getCircularSoundLevel(), 2, 0.00001, 10, 0.001);

    for (int i = 0; i < buffer.length / (config.getChannels() * 2); i++) {
      double angleLeft = 2 * Math.PI * config.getFrequency1() * i / SAMPLE_RATE;
      double angleRight = 2 * Math.PI * config.getFrequency2() * i / SAMPLE_RATE;

      switch (config.getChannels()) {
        case 1:
          double sample = leftVolume * (Math.sin(angleLeft) + Math.sin(angleRight)) / 2;
          short value = (short) (sample * Short.MAX_VALUE);
          buffer[config.getChannels() * 2 * i] = (byte) (value & 0xFF);
          buffer[config.getChannels() * 2 * i + 1] = (byte) ((value >> 8) & 0xFF);
          break;
        case 2:
          double sampleLeft = leftVolume * Math.sin(angleLeft);
          short valueLeft = (short) (sampleLeft * Short.MAX_VALUE);
          double sampleRight = rightVolume * Math.sin(angleRight);
          short valueRight = (short) (sampleRight * Short.MAX_VALUE);

          buffer[config.getChannels() * 2 * i] = (byte) (valueLeft & 0xFF);
          buffer[config.getChannels() * 2 * i + 1] = (byte) ((valueLeft >> 8) & 0xFF);
          buffer[config.getChannels() * 2 * i + 2] = (byte) (valueRight & 0xFF);
          buffer[config.getChannels() * 2 * i + 3] = (byte) ((valueRight >> 8) & 0xFF);
          break;
      }

      // Update volumes
      if (circularVolume) {
        if (leftVolume < minVolume || leftVolume > maxVolume) {
          dirLeft = -dirLeft;
        }
        if (rightVolume < minVolume || rightVolume > maxVolume) {
          dirRight = -dirRight;
        }
        leftVolume += dirLeft * step;
        rightVolume += dirRight * step;
      }
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
      volumeControl.setValue((volumeControl.getMaximum() - volumeControl.getMinimum()) * volume + volumeControl.getMinimum());
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
