package org.herbshouse.audio;

public class AudioPlayOrder {

  private final String filename;
  private final int milliseconds;
  private AudioPlayType type;
  private float volume = 1.0f;
  private AudioCallback callback;

  public AudioPlayOrder(String filename) {
    this(filename, -1);
    this.type = AudioPlayType.EFFECT;
  }

  public AudioPlayOrder(String filename, int milliseconds) {
    this.filename = filename;
    this.milliseconds = milliseconds;
    this.type = AudioPlayType.EFFECT;
  }

  public AudioCallback getCallback() {
    return callback;
  }

  public void setCallback(AudioCallback callback) {
    this.callback = callback;
  }

  public float getVolume() {
    return volume;
  }

  public void setVolume(float volume) {
    this.volume = volume;
  }

  public AudioPlayType getType() {
    return type;
  }

  public void setType(AudioPlayType type) {
    this.type = type;
  }

  public String getFilename() {
    return filename;
  }

  public int getMilliseconds() {
    return milliseconds;
  }
}
