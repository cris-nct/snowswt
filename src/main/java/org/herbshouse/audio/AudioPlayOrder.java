package org.herbshouse.audio;

public class AudioPlayOrder {

  private final String filename;
  private final int milliseconds;

  public AudioPlayOrder(String filename) {
    this(filename, -1);
  }

  public AudioPlayOrder(String filename, int milliseconds) {
    this.filename = filename;
    this.milliseconds = milliseconds;
  }

  public String getFilename() {
    return filename;
  }

  public int getMilliseconds() {
    return milliseconds;
  }
}
