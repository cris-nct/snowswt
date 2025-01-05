package org.herbshouse.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.enemies.RedFace;
import org.herbshouse.logic.snow.Snowflake;

public final class GuiUtils {

    public static final RGB FREEZE_COLOR = new RGB(0, 255, 255);

    public static final Rectangle SCREEN_BOUNDS = Display.getDefault().getBounds();

    private GuiUtils() {
    }

    public static RGB getPixelColor(ImageData imageData, int x, int y) {
        Point screenLoc = GuiUtils.toScreenCoord(x, y);
        if (screenLoc.x >= imageData.width || screenLoc.y >= imageData.height || screenLoc.x < 0 || screenLoc.y < 0) {
            return new RGB(0, 0, 0);
        } else {
            int actualPixel = imageData.getPixel(screenLoc.x, screenLoc.y);
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
            Point screenLoc = toScreenCoord(redFace.getLocation());
            gc.drawText(life,
                    (int) (screenLoc.x - textLength / 2.0d),
                    screenLoc.y,
                    true
            );
        }
    }

    public static void drawOval(GC gc, Point2D loc, Point2D size) {
        Point screenLoc = toScreenCoord(loc);
        gc.drawOval((int) (screenLoc.x - size.x / 2),
                (int) (screenLoc.y - size.y / 2),
                (int) size.x,
                (int) size.y
        );
    }

    public static void drawFillOval(GC gc, Point2D loc, double sizeX, double sizeY) {
        Point screenLoc = toScreenCoord(loc);
        gc.fillOval((int) (screenLoc.x - sizeX / 2),
                (int) (screenLoc.y - sizeY / 2),
                (int) sizeX,
                (int) sizeY
        );
    }

    public static void draw(GC gc, AbstractMovableObject movableObject, Point2D loc) {
        gc.setBackground(SWTResourceManager.getColor(movableObject.getColor()));
        gc.setForeground(SWTResourceManager.getColor(movableObject.getColor()));
        int prevAlpha = gc.getAlpha();
        gc.setAlpha(movableObject.getAlpha());

        Point screenObjLoc = toScreenCoord(loc);
        int locX = screenObjLoc.x - movableObject.getSize() / 2;
        int locY = screenObjLoc.y - movableObject.getSize() / 2;
        if (movableObject instanceof Snowflake snowflake && snowflake.isFreezed()) {
            locY = locY + movableObject.getSize() / 3;
        }
        gc.fillOval(locX, locY, movableObject.getSize(), movableObject.getSize());
        gc.setAlpha(prevAlpha);
    }

    public static Point toScreenCoord(Point2D loc) {
        return toScreenCoord(loc.x, loc.y);
    }

    public static Point toScreenCoord(double locX, double locY) {
        return new Point((int) locX, (int) (SCREEN_BOUNDS.height - locY));
    }

    public static Point2D toWorldCoord(int locX, double locY) {
        return new Point2D(locX, SCREEN_BOUNDS.height - locY);
    }

    public static Point2D toWorldCoord(Point loc) {
        return toWorldCoord(loc.x, loc.y);
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

    public static void moveMouseAndClick(int locX, int locY, int origLocX, int origLocY) {
        Event mouseMoveEvent = new Event();
        mouseMoveEvent.type = SWT.MouseMove;
        mouseMoveEvent.x = locX;
        mouseMoveEvent.y = locY;
        mouseMoveEvent.doit = true;
        Display.getDefault().post(mouseMoveEvent);

        Event mouseClick1 = new Event();
        mouseClick1.type = SWT.MouseDown;
        mouseClick1.button = 1;
        mouseClick1.doit = true;
        Display.getDefault().post(mouseClick1);

        Event mouseClick2 = new Event();
        mouseClick2.type = SWT.MouseUp;
        mouseClick2.button = 1;
        mouseClick2.doit = true;
        Display.getDefault().post(mouseClick2);

        Event moveToOrig = new Event();
        moveToOrig.type = SWT.MouseMove;
        moveToOrig.x = origLocX;
        moveToOrig.y = origLocY;
        moveToOrig.doit = true;
        Display.getDefault().post(moveToOrig);
    }

    public static int[] toScreenCoord(double[] circlePoints) {
        int[] result = new int[circlePoints.length];
        for (int i = 0; i < circlePoints.length; i += 2) {
            Point screenLoc = GuiUtils.toScreenCoord(new Point2D(circlePoints[i], circlePoints[i + 1]));
            result[i] = screenLoc.x;
            result[i + 1] = screenLoc.y;
        }
        return result;
    }
}
