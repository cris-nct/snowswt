package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.GuiUtils;

class TextDrawer {

  public void drawCenteredText(GC gc, String text, Font font, int color) {
    gc.setForeground(Display.getDefault().getSystemColor(color));
    gc.setFont(font);
    Point textSize = gc.stringExtent(text);
    gc.drawText(text, (GuiUtils.SCREEN_BOUNDS.width - textSize.x) / 2,
        (GuiUtils.SCREEN_BOUNDS.height - textSize.y) / 2, true);
  }
}