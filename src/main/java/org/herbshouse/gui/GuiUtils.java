package org.herbshouse.gui;

import org.eclipse.swt.graphics.*;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Snowflake;

public final class GuiUtils {

    private GuiUtils() {
    }

    public static RGB getPixelColor(ImageData imageData, int x, int y) {
        if (x >= imageData.width || y >= imageData.height || x < 0 || y < 0) {
            return new RGB(0, 0, 0);
        } else {
            int actualPixel = imageData.getPixel(x, y);
            return new RGB((actualPixel >> 24) & 0xFF, (actualPixel >> 16) & 0xFF, (actualPixel >> 8) & 0xFF);
        }
    }

    public static void drawTextInMiddleOfScreen(GC gc, String text) {
        Point textSize = gc.stringExtent(text);
        Rectangle drawingSurface = gc.getClipping();
        gc.drawText(text, (drawingSurface.width - textSize.x) / 2, (drawingSurface.height - textSize.y) / 2, true);
    }

    public static void drawSnowflake(GC gc, Snowflake snowflake) {
        drawSnowflake(gc, snowflake, snowflake.getLocation());
    }

    public static void drawSnowflake(GC gc, Snowflake snowflake, Point2D loc) {
        gc.setBackground(SWTResourceManager.getColor(snowflake.getColor()));
        gc.fillOval(
                (int) loc.x - snowflake.getSize() / 2,
                (int) loc.y - snowflake.getSize() / 2,
                snowflake.getSize(),
                snowflake.getSize()
        );
    }

    public static void drawSnowflakeAsMercedes(GC gc, Snowflake snowflake) {
        gc.drawImage(SnowingApplication.mbImageSmall,
                (int) snowflake.getLocation().x - SnowingApplication.MB_ICON_SIZE / 2,
                (int) snowflake.getLocation().y - SnowingApplication.MB_ICON_SIZE / 2
        );
    }

}
