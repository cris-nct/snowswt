package org.herbshouse.audio;

public class AudioPlayOrder {

  private final String filename;
  private AudioPlayType type;
  private final int milliseconds;

  public AudioPlayOrder(String filename) {
    this(filename, -1);
    this.type = AudioPlayType.EFFECT;
  }

  public AudioPlayOrder(String filename, int milliseconds) {
    this.filename = filename;
    this.milliseconds = milliseconds;
    this.type = AudioPlayType.EFFECT;
  }

  public void setType(AudioPlayType type) {
    this.type = type;
  }

  public AudioPlayType getType() {
    return type;
  }

  public String getFilename() {
    return filename;
  }

  public int getMilliseconds() {
    return milliseconds;
  }
}
