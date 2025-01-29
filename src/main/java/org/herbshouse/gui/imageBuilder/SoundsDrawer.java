package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.graphicalSounds.GraphicalSound;
import org.herbshouse.logic.graphicalSounds.GraphicalSoundsGenerator;
import org.herbshouse.logic.graphicalSounds.data.GraphicalSoundData;

public class SoundsDrawer {

  public void draw(GC gc, GraphicalSoundsGenerator generator) {
    gc.setAdvanced(true);
    gc.setAntialias(SWT.ON);
    for (GraphicalSound sound : generator.getMoveableObjects()) {
      GraphicalSoundData data = (GraphicalSoundData) sound.getData("SOUNDSUPDATER");
      if (data == null) {
        return;
      }
      gc.setForeground(SWTResourceManager.getColor(sound.getColor()));
      Point2D lastPoint = null;
      for (Point2D point : data.getPoints()) {
        if (lastPoint == null) {
          lastPoint = point;
          continue;
        }
        if (point.x <= 1) {
          lastPoint = point;
          continue;
        }
        GuiUtils.drawLine(gc, lastPoint, point, 1);
        lastPoint = point;
      }
      if (generator.getFlagsConfiguration().getGraphicalSoundConfig().isSlowPlay()) {
        gc.setBackground(SWTResourceManager.getColor(new RGB(255, 0, 0)));
        GuiUtils.drawFillOval(gc, lastPoint, 20, 20);
      }
    }
    gc.setAntialias(SWT.DEFAULT);
  }

}
