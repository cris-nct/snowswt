package org.herbshouse.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.redface.RedFace;

public final class GuiUtils {

    public static final RGB FREEZE_COLOR = new RGB(0, 255, 255);

    private GuiUtils() {
    }

    public static RGB getPixelColor(ImageData imageData, int x, int y) {
        if (x >= imageData.width || y >= imageData.height || x < 0 || y < 0) {
            return new RGB(0, 0, 0);
        } else {
            int actualPixel = imageData.getPixel(x, y);
            return imageData.palette.getRGB(actualPixel);
        }
    }

    public static void draw(GC gc, AbstractMovableObject movableObject) {
        draw(gc, movableObject, movableObject.getLocation());
    }

    public static void drawRedFace(GC gc, RedFace redFace) {
        //Draw background
        draw(gc, redFace);

        //Draw eyes border
        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

        gc.setLineWidth(2);
        drawOval(gc, redFace.getLeftEyeLoc(), redFace.getEyesSize());
        drawOval(gc, redFace.getRightEyeLoc(), redFace.getEyesSize());

        //Draw left eye pupil
        drawFillOval(gc, redFace.getLeftPupilLoc(), redFace.getPupilSize(), redFace.getPupilSize());

        //Draw right eye pupil
        drawFillOval(gc, redFace.getRightPupilLoc(), redFace.getPupilSize(), redFace.getPupilSize());

        //Draw life counter
        if (redFace.getKissingGif() == null) {
            String life = String.valueOf(redFace.getLife());
            gc.setFont(SWTResourceManager.getFont("Arial", 15, SWT.NORMAL));
            int textLength = gc.textExtent(life).x;
            gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            gc.drawText(life,
                    (int) (redFace.getLocation().x - textLength / 2.0d),
                    (int) (redFace.getLocation().y),
                    true
            );
        }
    }

    public static void drawOval(GC gc, Point2D loc, Point2D size) {
        gc.drawOval((int) (loc.x - size.x / 2),
                (int) (loc.y - size.y / 2),
                (int) size.x,
                (int) size.y
        );
    }

    public static void drawFillOval(GC gc, Point2D loc, double sizeX, double sizeY) {
        gc.fillOval((int) (loc.x - sizeX / 2),
                (int) (loc.y - sizeY / 2),
                (int) sizeX,
                (int) sizeY
        );
    }

    public static void drawFillArc(GC gc, double locX, double locY, double sizeX, double sizeY, double startAngle, double endAngle) {
        gc.fillArc((int) (locX - sizeX / 2),
                (int) (locY - sizeY / 2),
                (int) sizeX,
                (int) sizeY,
                (int) startAngle,
                (int) endAngle
        );
    }

    public static void draw(GC gc, AbstractMovableObject movableObject, Point2D loc) {
        gc.setBackground(SWTResourceManager.getColor(movableObject.getColor()));
        int prevAlpha = gc.getAlpha();
        gc.setAlpha(movableObject.getAlpha());
        gc.fillOval(
                (int) loc.x - movableObject.getSize() / 2,
                (int) loc.y - movableObject.getSize() / 2,
                movableObject.getSize(),
                movableObject.getSize()
        );
        gc.setAlpha(prevAlpha);
    }

    public static void drawSnowflakeAsMercedes(GC gc, AbstractMovableObject snowflake) {
        gc.drawImage(SnowingApplication.mbImageSmall,
                (int) snowflake.getLocation().x - SnowingApplication.MB_ICON_SIZE / 2,
                (int) snowflake.getLocation().y - SnowingApplication.MB_ICON_SIZE / 2
        );
    }

    public static boolean equalsColors(RGB color1, RGB color2, int eps) {
        return Math.abs(color1.red - color2.red) <= eps
                && Math.abs(color1.green - color2.green) <= eps
                && Math.abs(color1.blue - color2.blue) <= eps;
    }
}
