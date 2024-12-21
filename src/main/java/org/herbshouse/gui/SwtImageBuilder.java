package org.herbshouse.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.SnowGenerator;
import org.herbshouse.logic.Snowflake;
import org.herbshouse.logic.Point2D;

public class SwtImageBuilder implements AutoCloseable {
    private final GC originalGC;
    private GC gcImage;
    private Transform transform;
    private Image image;

    public SwtImageBuilder(GC gc) {
        originalGC = gc;
    }

    public Image createImage(SnowGenerator snowGenerator, boolean flipImage, boolean debug) {
        Rectangle totalArea = originalGC.getClipping();
        image = new Image(Display.getDefault(), totalArea);
        gcImage = new GC(image);
        gcImage.setAdvanced(true);
        gcImage.setAntialias(SWT.DEFAULT);
        gcImage.setTextAntialias(SWT.ON);

        if (flipImage) {
            transform = new Transform(Display.getDefault());
            transform.scale(1, -1);
            transform.translate(0, -totalArea.height);
            gcImage.setTransform(transform);
        }

        //Draw background
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gcImage.fillRectangle(0, 0, totalArea.width, totalArea.height);

        //Draw test
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
        String text = "Happy New Year!";
        gcImage.setFont(SWTResourceManager.getFont("Arial", 25, SWT.BOLD));
        Point textSize = gcImage.stringExtent(text);
        gcImage.drawText(text, (totalArea.width-textSize.x)/2, (totalArea.height-textSize.y)/2, true);

        //Draw snowflakes
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        for (Snowflake snowflake : snowGenerator.getSnowflakes()) {
            GuiUtils.drawSnowflake(gcImage, snowflake);
            if (debug) {
                for (Point2D loc : snowflake.getHistoryLocations()) {
                    GuiUtils.drawSnowflake(gcImage, snowflake, loc);
                }
            }
        }
        return image;
    }

    @Override
    public void close() {
        if (transform != null) {
            transform.dispose();
        }
        gcImage.dispose();
        image.dispose();
    }

}
