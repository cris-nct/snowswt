package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

class MinimapDrawer {

  public void draw(GC gc, Image image) {
    //Draw minimap
    ImageData imageData = image.getImageData();
    double aspRatio = (double) imageData.width / imageData.height;
    int heightMinimap = 200;
    int widthMinimap = (int) (heightMinimap * aspRatio);
    gc.setAlpha(180);
    gc.drawImage(image, 0, 0, imageData.width, imageData.height,
        0, imageData.height - heightMinimap, widthMinimap, heightMinimap);
    gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    gc.drawRectangle(0, imageData.height - heightMinimap, widthMinimap, heightMinimap - 1);
    gc.setAlpha(255);
  }

}
