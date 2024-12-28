package org.herbshouse.gui;

import org.eclipse.swt.graphics.*;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;

public final class GuiUtils {

    public static final RGB FREEZE_COLOR = new RGB(0, 255, 255);

    private GuiUtils() {
    }

    public static RGB getPixelColor(ImageData imageData, int x, int y) {
        if (x >= imageData.width || y >= imageData.height || x < 0 || y < 0) {
            return new RGB(0, 0, 0);
        } else {
            int actualPixel = imageData.getPixel(x, y);
            return new RGB((actualPixel >> 8) & 0xFF, (actualPixel >> 16) & 0xFF, (actualPixel >> 24) & 0xFF);
        }
    }

    public static void draw(GC gc, AbstractMovableObject snowflake) {
        draw(gc, snowflake, snowflake.getLocation());
    }

    public static void draw(GC gc, AbstractMovableObject snowflake, Point2D loc) {
        gc.setBackground(SWTResourceManager.getColor(snowflake.getColor()));
        int prevAlpha = gc.getAlpha();
        gc.setAlpha(snowflake.getAlpha());
        gc.fillOval(
                (int) loc.x - snowflake.getSize() / 2,
                (int) loc.y - snowflake.getSize() / 2,
                snowflake.getSize(),
                snowflake.getSize()
        );
        gc.setAlpha(prevAlpha);
    }

    public static void drawSnowflakeAsMercedes(GC gc, AbstractMovableObject snowflake) {
        gc.drawImage(SnowingApplication.mbImageSmall,
                (int) snowflake.getLocation().x - SnowingApplication.MB_ICON_SIZE / 2,
                (int) snowflake.getLocation().y - SnowingApplication.MB_ICON_SIZE / 2
        );
    }

}
