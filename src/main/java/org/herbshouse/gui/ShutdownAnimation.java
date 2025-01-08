package org.herbshouse.gui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.enemies.AnimatedGif;

public class ShutdownAnimation {

  private final List<Integer> valuesToAlternate = Arrays.asList(-6, 86);

  private final LinkedList<String> messages = new LinkedList<>();
  private final Shell shell;
  private final AnimatedGif flame;
  private int phaseIndexShacking = 0;
  private int alphaMainImage = 255;
  private boolean startCrackFading;
  private int alphaCrackImage = 255;
  private int counterNewMessage;
  private String message;

  public ShutdownAnimation(Shell shell) {
    this.messages.addAll(Arrays.asList("Now ", "Now what ", "Now what will ", "Now what will you ",
        "Now what will you do?"));
    this.shell = shell;
    this.flame = new AnimatedGif("fire-flame.gif", 2, null);
    this.flame.setLocation(new Point2D(GuiUtils.SCREEN_BOUNDS.width / 2.0, 50));
    this.flame.setSize(100);
  }

  public void draw(GC gc, Image image) {
    if (alphaMainImage < 20) {
      this.drawText(gc);
    } else {
      this.drawImage(gc, image);
    }
    this.drawFlame(gc);
  }

  private void drawImage(GC gc, Image image) {
    ImageData imageData = image.getImageData();
    double aspRatio = (double) imageData.width / imageData.height;
    int heightMinimap =
        GuiUtils.SCREEN_BOUNDS.height - 2 * valuesToAlternate.get(phaseIndexShacking);
    int widthMinimap = (int) (heightMinimap * aspRatio);
    if (heightMinimap > 1 && widthMinimap > 1) {
      //Draw the main image
      gc.setAlpha(alphaMainImage -= 7);
      int locXMainImage = (GuiUtils.SCREEN_BOUNDS.width - widthMinimap) / 2;
      int locYMainImage = (GuiUtils.SCREEN_BOUNDS.height - heightMinimap) / 2;
      gc.drawImage(image, 0, 0, imageData.width, imageData.height,
          locXMainImage, locYMainImage, widthMinimap, heightMinimap);
      phaseIndexShacking = ++phaseIndexShacking % valuesToAlternate.size();
      if (alphaMainImage < 100) {
        startCrackFading = true;
      }
    }

    //Draw the blue crack
    if (startCrackFading) {
      alphaCrackImage -= 15;
    }
    gc.setAlpha(alphaCrackImage);
    Image imageBlueCrack
        = SWTResourceManager.getImage(SnowingApplication.class, "crack-1080.png", true);
    ImageData imageDataCrack = imageBlueCrack.getImageData();
    int locXCrack = (imageData.width - imageDataCrack.width) / 2 - 230;
    int locYCrack = (imageData.height - imageDataCrack.height) / 2;
    gc.drawImage(imageBlueCrack, locXCrack, locYCrack);
  }

  private void drawText(GC gc) {
    gc.setFont(SWTResourceManager.getFont("Arial", 90, SWT.BOLD));
    gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    gc.setTextAntialias(SWT.DEFAULT);
    gc.setAdvanced(true);
    if (counterNewMessage % 20 == 0 && !messages.isEmpty()) {
      counterNewMessage = 0;
      message = messages.pop();
    }
    Point textSize = gc.stringExtent(message);
    gc.drawText(message,
        (GuiUtils.SCREEN_BOUNDS.width - textSize.x) / 2,
        (GuiUtils.SCREEN_BOUNDS.height - textSize.y) / 2,
        true
    );
    counterNewMessage++;
    if (messages.isEmpty()) {
      Display.getDefault().timerExec(1000, shell::dispose);
    }
  }

  private void drawFlame(GC gc) {
    Image img = SWTResourceManager.getGif(SnowingApplication.class,
        flame.getFilename(),
        flame.getImageIndex(),
        flame.getSize(),
        flame.getSize(),
        flame.getRemoveBackgroundColor(),
        true
    );
    Point screenLoc = GuiUtils.toScreenCoord(flame.getLocation());
    screenLoc.x -= flame.getSize() / 2;
    screenLoc.y -= flame.getSize() / 2;
    gc.setAlpha(255);
    gc.drawImage(img, screenLoc.x, screenLoc.y);
    flame.increaseImageIndex();
  }

}
