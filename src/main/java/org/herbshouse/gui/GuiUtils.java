package org.herbshouse.gui;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.herbshouse.logic.Snowflake;
import org.herbshouse.logic.Point2D;

public final class GuiUtils {

    private GuiUtils(){

    }

    public static RGB getPixelColor(ImageData imageData, int x, int y){
        if (x >= imageData.width || y >= imageData.height || x < 0 || y < 0) {
            return new RGB(0,0,0);
        } else {
            int actualPixel = imageData.getPixel(x, y);
            return new RGB((actualPixel >> 24) & 0xFF, (actualPixel >> 16) & 0xFF, (actualPixel >> 8) & 0xFF);
        }
    }


    public static void drawSnowflake(GC gc, Snowflake snowflake){
        gc.fillOval(
                (int) snowflake.getLocation().x - snowflake.getSize() / 2,
                (int) snowflake.getLocation().y - snowflake.getSize() / 2,
                snowflake.getSize(),
                snowflake.getSize()
        );
    }

    public static void drawSnowflake(GC gc, Snowflake snowflake, Point2D loc){
        gc.fillOval(
                (int) loc.x - snowflake.getSize() / 2,
                (int) loc.y - snowflake.getSize() / 2,
                snowflake.getSize(),
                snowflake.getSize()
        );
    }

}
