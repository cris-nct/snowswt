package org.herbshouse.logic.graphicalSounds;

import java.util.HashMap;
import java.util.Map;
import org.herbshouse.audio.SoundUtils;
import org.herbshouse.controller.GraphicalSoundConfig;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.graphicalSounds.data.GraphicalSoundData;
import org.herbshouse.logic.graphicalSounds.data.SoundData;

public class GraphicalSound extends AbstractMovableObject {

  private final Map<String, SoundData> data = new HashMap<>();

  private final byte[] audioBuffer;

  private final GraphicalSoundConfig config;

  public GraphicalSound(GraphicalSoundConfig config) {
    this.config = config;
    this.audioBuffer = SoundUtils.modulateFrequencies(config);
  }

  public GraphicalSoundConfig getConfig() {
    return config;
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
