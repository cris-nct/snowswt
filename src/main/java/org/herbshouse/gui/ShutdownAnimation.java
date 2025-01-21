package org.herbshouse.gui;

import java.util.ArrayList;
import java.util.Arrays;
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

  private static final int ALPHA_DECREASE_RATE = 7;
  private static final int CRACK_ALPHA_DECREASE_RATE = 25;
  private static final String MESSAGE = "It's time for new challenge";

  private final List<Integer> valuesToAlternate = Arrays.asList(-30, 86);
  private final ArrayList<String> messages = new ArrayList<>();
  private final Shell shell;
  private final AnimatedGif flame;
  private final Image glassCrackImage;
  private final Image blueCrackImage;

  private int phaseIndexShacking = 0;
  private int alphaMainImage = 255;
  private int alphaCrackImage = 255;
  private int counterNewMessage;
  private boolean startCrackFading;
  private String message;

  public ShutdownAnimation(Shell shell) {
    String prev = null;
    for (String msgpart : MESSAGE.split(" ")) {
      if (prev == null) {
        prev = msgpart;
      } else {
        prev += " " + msgpart;
      }
      this.messages.add(prev);
    }
    this.shell = shell;
    this.flame = new AnimatedGif("pictures/fire-flame.gif", 2, null);
    this.flame.setLocation(new Point2D(GuiUtils.SCREEN_BOUNDS.width / 2.0, 50));
    this.flame.setSize(100);
    this.glassCrackImage = SWTResourceManager.getImage(SnowingApplication.class, "pictures/glasscrack.png",
        true);
    this.blueCrackImage = SWTResourceManager.getImage(SnowingApplication.class, "pictures/crack.png",
        true);
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
    //Draw the main image
    ImageData imageData = image.getImageData();
    double aspRatio = (double) imageData.width / imageData.height;
    int heightMinimap =
        GuiUtils.SCREEN_BOUNDS.height - 2 * valuesToAlternate.get(phaseIndexShacking);
    int widthMinimap = (int) (heightMinimap * aspRatio);
    if (heightMinimap > 1 && widthMinimap > 1) {
      gc.setAlpha(alphaMainImage);
      int locXMainImage = (GuiUtils.SCREEN_BOUNDS.width - widthMinimap) / 2;
      int locYMainImage = (GuiUtils.SCREEN_BOUNDS.height - heightMinimap) / 2;
      gc.drawImage(image, 0, 0, imageData.width, imageData.height,
          locXMainImage, locYMainImage, widthMinimap, heightMinimap);
      phaseIndexShacking = ++phaseIndexShacking % valuesToAlternate.size();
    }

    //Draw the blue crack
    gc.setAlpha(alphaCrackImage);
    ImageData imageDataCrack = blueCrackImage.getImageData();
    int locXCrack = (imageData.width - imageDataCrack.width) / 2 - 230;
    int locYCrack = (imageData.height - imageDataCrack.height) / 2;
    gc.drawImage(blueCrackImage, locXCrack, locYCrack);

    //Draw glass crack
    gc.drawImage(glassCrackImage, 0, 0);
    gc.drawImage(glassCrackImage, 700, 500);
    gc.drawImage(glassCrackImage, 1480, 100);

    this.updateAlphaValues();
  }

  private void updateMessage() {
    if (counterNewMessage % 20 == 0 && !messages.isEmpty()) {
      counterNewMessage = 0;
      message = messages.removeFirst();
    }
    counterNewMessage++;
  }

  private void updateAlphaValues() {
    // Update the main image alpha
    if (alphaMainImage > 0) {
      alphaMainImage = Math.max(0, alphaMainImage - ALPHA_DECREASE_RATE);
    }
    if (alphaMainImage < 100) {
      startCrackFading = true;
    }
    // Update the crack image alpha if fading has started
    if (startCrackFading && alphaCrackImage > 0) {
      alphaCrackImage = Math.max(0, alphaCrackImage - CRACK_ALPHA_DECREASE_RATE);
    }
  }

  private void drawText(GC gc) {
    gc.setFont(SWTResourceManager.getFont("Arial", 90, SWT.BOLD));
    gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    gc.setTextAntialias(SWT.DEFAULT);
    gc.setAdvanced(true);
    this.updateMessage();
    Point textSize = gc.stringExtent(message);
    gc.drawText(message,
        (GuiUtils.SCREEN_BOUNDS.width - textSize.x) / 2,
        (GuiUtils.SCREEN_BOUNDS.height - textSize.y) / 2,
        true
    );
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
