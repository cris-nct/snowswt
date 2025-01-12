package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;

class CountdownDrawer {

  public void draw(
      GC gcImage,
      GeneratorListener<? extends AbstractMovableObject> generatorListener,
      String textFromScreen
  ) {
    if (gcImage == null) {
      throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
    }
    //Draw countdown
    if (generatorListener.getCountdown() >= 0) {
      if (generatorListener.getCountdown() >= 4) {
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
      } else {
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
      }
      String countdown = String.valueOf(generatorListener.getCountdown());
      Point countdownSize = gcImage.stringExtent(countdown);
      Point textSize = gcImage.stringExtent(textFromScreen);
      gcImage.drawText(countdown, (GuiUtils.SCREEN_BOUNDS.width - countdownSize.x) / 2,
          GuiUtils.SCREEN_BOUNDS.height / 2 + textSize.y, true);
    }
  }

}
