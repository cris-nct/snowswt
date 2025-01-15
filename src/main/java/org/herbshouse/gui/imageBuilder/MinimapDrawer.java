package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

class MinimapDrawer {

  private static final int HEIGHT_MINIMAP = 200;

  public void draw(GC gc, Image image) {
    //Draw minimap
    ImageData imageData = image.getImageData();
    double aspRatio = (double) imageData.width / imageData.height;

    int widthMinimap = (int) (HEIGHT_MINIMAP * aspRatio);
    gc.setAlpha(180);
    gc.drawImage(image, 0, 0, imageData.width, imageData.height,
        0, imageData.height - HEIGHT_MINIMAP, widthMinimap, HEIGHT_MINIMAP);
    gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    gc.drawRectangle(0, imageData.height - HEIGHT_MINIMAP, widthMinimap, HEIGHT_MINIMAP - 1);
    gc.setAlpha(255);
  }

}
