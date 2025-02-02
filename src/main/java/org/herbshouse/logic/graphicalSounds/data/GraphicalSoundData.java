package org.herbshouse.logic.graphicalSounds.data;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.herbshouse.audio.SoundUtils;
import org.herbshouse.logic.CircularQueue;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.graphicalSounds.GraphicalSound;

public class GraphicalSoundData implements SoundData {

  private static final boolean BALANCE_HIGH_LEVEL = false;
  private static final float VOLUME = 0.8f;

  private int currentIndex = -1;

  private int currentRow;

  private final CircularQueue<Point2D> visiblePoints;

  private final SourceDataLine line;

  private final GraphicalSound sound;

  private final byte[] bytesToFlush;

  private int byteToFlashCounter = 0;

  private float balance = 0f;

  private float balanceDir = 1;

  public GraphicalSoundData(GraphicalSound sound, int size) {
    this.sound = sound;
    this.visiblePoints = new CircularQueue<>(size / getStepVisualPoints());
    AudioFormat format = new AudioFormat(
        SoundUtils.SAMPLE_RATE,
        SoundUtils.SAMPLE_SIZE_BYTES,
        sound.getConfig().getChannels(),
        SoundUtils.SIGNED,
        SoundUtils.BIG_ENDIAN
    );
    bytesToFlush = new byte[format.getFrameSize()];
    try {
      line = AudioSystem.getSourceDataLine(format);
      line.open(format);
      this.setVolume(VOLUME);
      line.start();
    } catch (LineUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private int getStepVisualPoints() {
    return sound.getConfig().getChannels() * 2;
  }

  public void setVolume(float volume) {
    FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
    volumeControl.setValue(
        (volumeControl.getMaximum() - volumeControl.getMinimum()) * volume + volumeControl.getMinimum());
  }

  public void setBalance(float balance) {
    FloatControl balanceControl = (FloatControl) line.getControl(FloatControl.Type.BALANCE);
    balanceControl.setValue(balance);
  }

  public void addPoint(Point2D point) {
    if (currentIndex % getStepVisualPoints() == 0) {
      visiblePoints.offer(point);
    }
    currentIndex++;
    bytesToFlush[byteToFlashCounter++] = sound.getAudioBuffer()[currentIndex];
    if (byteToFlashCounter == bytesToFlush.length) {
      byteToFlashCounter = 0;
      line.write(bytesToFlush, 0, bytesToFlush.length);
    }

    if (currentIndex + 1 >= sound.getAudioBuffer().length) {
      line.drain();
      line.close();
    }

    if (BALANCE_HIGH_LEVEL) {
      float stepBalance = 0.0001f;
      balance += balanceDir * stepBalance;
      if (balance < -1 || balance > 1) {
        balanceDir = -balanceDir;
      } else {
        setBalance(balance);
      }
    }
  }

  public CircularQueue<Point2D> getPoints() {
    return visiblePoints;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void clear() {
    line.drain();
    line.close();
    visiblePoints.clear();
  }

  public int getCurrentRow() {
    return currentRow;
  }

  public int increaseRow() {
    return ++currentRow;
  }

  public int resetRow() {
    currentRow = 0;
    return currentRow;
  }
}
