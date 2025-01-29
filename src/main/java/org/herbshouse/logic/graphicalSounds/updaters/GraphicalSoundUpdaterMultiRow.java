package org.herbshouse.logic.graphicalSounds.updaters;

import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.graphicalSounds.GraphicalSound;
import org.herbshouse.logic.graphicalSounds.data.GraphicalSoundData;

public class GraphicalSoundUpdaterMultiRow extends AbstractGraphicalSoundUpdater {

  public GraphicalSoundUpdaterMultiRow(Rectangle screenBounds) {
    super(screenBounds);
  }

  @Override
  public boolean update(GraphicalSound sound) {
    GraphicalSoundData data = getUpdaterData(sound);
    byte[] audioBuffer = sound.getAudioBuffer();
    int index = data.getCurrentIndex();
    final boolean stillAlive;
    if (index + 1 < audioBuffer.length) {
      stillAlive = true;
      index++;
      int row = data.getCurrentRow();
      if (index % screenBounds.width == 0) {
        row = data.increaseRow();
        if ((row + 1) * sound.getSize() > screenBounds.height) {
          row = data.resetRow();
        }
      }
      double locX = sound.getLocation().x + index % screenBounds.width;
      double locY = screenBounds.height - sound.getSize() / 2.0
          + Utils.linearInterpolation(audioBuffer[index], -127, -sound.getSize() / 2.0, 127, sound.getSize() / 2.0)
          - row * sound.getSize();
      data.addPoint(new Point2D(locX, locY));
    } else {
      stillAlive = false;
      sound.setData("SOUNDSUPDATER", null);
    }
    return stillAlive;
  }

  private GraphicalSoundData getUpdaterData(GraphicalSound sound) {
    GraphicalSoundData data = (GraphicalSoundData) sound.getData("SOUNDSUPDATER");
    if (data == null) {
      data = new GraphicalSoundData(sound, screenBounds.width * screenBounds.height / sound.getSize());
      sound.setData("SOUNDSUPDATER", data);
    }
    return data;
  }

}
