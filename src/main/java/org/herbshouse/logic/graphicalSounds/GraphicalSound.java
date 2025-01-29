package org.herbshouse.logic.graphicalSounds;

import java.util.HashMap;
import java.util.Map;
import org.herbshouse.audio.SoundUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.graphicalSounds.data.GraphicalSoundData;
import org.herbshouse.logic.graphicalSounds.data.SoundData;

public class GraphicalSound extends AbstractMovableObject {

  private final Map<String, SoundData> data = new HashMap<>();

  private final int durationSec;

  private int step;

  private final int frequency1;

  private final int frequency2;

  private final byte[] audioBuffer;

  public GraphicalSound(int durationSec, int frequency) {
    this.durationSec = durationSec;
    this.frequency1 = frequency;
    this.frequency2 = -1;
    this.audioBuffer = SoundUtils.modulateFrequencies(durationSec, frequency);
  }

  public GraphicalSound(int durationSec, int frequency1, int frequency2) {
    this.durationSec = durationSec;
    this.frequency1 = frequency1;
    this.frequency2 = frequency2;
    this.audioBuffer = SoundUtils.modulateFrequencies(durationSec, frequency1, frequency2);
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
