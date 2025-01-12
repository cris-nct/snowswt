package org.herbshouse.gui.imageBuilder;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.controller.LogicController;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;

class SnowflakesDrawer {

  private final LogicController controller;

  public SnowflakesDrawer(LogicController controller) {
    this.controller = controller;
  }

  public void draw(GC gcImage, GeneratorListener<Snowflake> generatorListener) {
    if (gcImage == null) {
      throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
    }
    //Draw snowflakes
    gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
    List<Snowflake> snowflakes = generatorListener.getMoveableObjects();
    FlagsConfiguration config = controller.getFlagsConfiguration();
    for (Snowflake snowflake : snowflakes) {
      if (config.isMercedesSnowflakes()) {
        GuiUtils.drawSnowflakeAsMercedes(gcImage, snowflake);
      } else {
        GuiUtils.draw(gcImage, snowflake);
      }
      if (!snowflake.isFreezed() && (config.isDebug() || config.isObjectsTail())) {
        int counterAlpha = 0;
        int origAlpha = snowflake.getAlpha();
        int points = snowflake.getSnowTail().getHistoryLocations().size();
        for (Point2D oldLoc : snowflake.getSnowTail().getHistoryLocations()) {
          snowflake.setAlpha((int) Utils.linearInterpolation(counterAlpha, 0, 0, points, 80));
          GuiUtils.draw(gcImage, snowflake, oldLoc);
          counterAlpha++;
        }
        snowflake.setAlpha(origAlpha);
      }
    }
  }
}
