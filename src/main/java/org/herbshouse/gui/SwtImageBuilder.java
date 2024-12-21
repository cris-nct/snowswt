package org.herbshouse.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
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

    public Image createImage(SnowGenerator snowGenerator, boolean flipImage) {
        Rectangle totalArea = originalGC.getClipping();
        image = new Image(Display.getDefault(), totalArea);
        gcImage = new GC(image);
        gcImage.setAdvanced(true);
        gcImage.setAntialias(SWT.DEFAULT);

        if (flipImage) {
            transform = new Transform(Display.getDefault());
            transform.scale(1, -1);
            transform.translate(0, -totalArea.height);
            gcImage.setTransform(transform);
        }

        //Draw background
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gcImage.fillRectangle(0, 0, totalArea.width, totalArea.height);

        //Draw house
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
        gcImage.fillRectangle(0, totalArea.height - 100, totalArea.width, 1);

        //Draw snowflakes
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        for (Snowflake snowflake : snowGenerator.getSnowflakes()) {
            GuiUtils.drawSnowflake(gcImage, snowflake);
            if (SnowingApplication.DEBUG_PATH) {
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
