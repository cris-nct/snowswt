package org.herbshouse.logic.graphicalSounds;

import java.util.HashMap;
import java.util.Map;
import org.herbshouse.audio.SoundUtils;
import org.herbshouse.controller.GraphicalSoundConfig;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.graphicalSounds.data.GraphicalSoundData;
import org.herbshouse.logic.graphicalSounds.data.SoundData;

public class GraphicalSound extends AbstractMovableObject {

  private final Map<String, SoundData> data = new HashMap<>();

  private final int durationSec;

  private int step;

  private final int frequency1;

  private final int frequency2;

  private final byte[] audioBuffer;

  public GraphicalSound(GraphicalSoundConfig config) {
    this.durationSec = config.getDuration();
    this.frequency1 = config.getFrequency1();
    this.frequency2 = config.getFrequency2();
    if (config.getCircularSoundLevel() == 1) {
      this.audioBuffer = SoundUtils.modulateFrequencies(durationSec, frequency1, frequency2, 1.0f, 1.0f);
    } else {
      float step = (float) Utils.linearInterpolation(config.getCircularSoundLevel(), 2, 0.00001, 10, 0.001);
      this.audioBuffer = SoundUtils.circularSound(durationSec, step, frequency1, frequency2);
    }
  }

  public GraphicalSound(int durationSec, int step, int frequency1, int frequency2) {
    int minDuration = 4 * step * step;
    if (durationSec < minDuration) {
      throw new IllegalArgumentException("Duration too small. Should be at least " + minDuration);
    }
    this.durationSec = durationSec;
    this.step = step;
    this.frequency1 = frequency1;
    this.frequency2 = frequency2;
    this.audioBuffer = SoundUtils.modulateTwoFrequencies(durationSec, step, frequency1, frequency2);
  }

  public int getFrequency1() {
    return frequency1;
  }

  public int getFrequency2() {
    return frequency2;
  }

  public int getDurationSec() {
    return durationSec;
  }

  public byte[] getAudioBuffer() {
    return audioBuffer;
  }

  public void setData(String key, SoundData value) {
    data.put(key, value);
  }

  public SoundData getData(String key) {
    return data.get(key);
  }

  public void cleanup() {
    for (SoundData d : data.values()) {
      if (d instanceof GraphicalSoundData soundData) {
        soundData.clear();
      }
    }
    data.clear();
  }

}
