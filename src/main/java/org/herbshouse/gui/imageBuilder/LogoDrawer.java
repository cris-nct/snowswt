package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.herbshouse.SnowingApplication;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.logic.Utils;

public class LogoDrawer {

  private int counterLogo;

  public void draw(GC gc) {
    if (gc == null) {
      throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
    }
    int alphaMB = (int) Utils.linearInterpolation(
        Math.sin(Math.toRadians((counterLogo += 5) % 360)),
        -1, 30, 1, 240
    );
    gc.setAlpha(alphaMB);
    Image mbImage = SWTResourceManager.getSVG(SnowingApplication.class, "pictures/mb.svg", 200, 200, true);
    gc.drawImage(mbImage, 0, 0);
    gc.setAlpha(255);
  }
}
