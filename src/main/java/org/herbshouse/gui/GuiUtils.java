package org.herbshouse.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
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
        double gapEyesX = 0.15 * redFace.getSize();
        double gapEyesY = 0.2 * redFace.getSize();
        double eyesSizeX = 0.2 * redFace.getSize();
        double eyesSizeY = 0.7 * eyesSizeX;
        double pupilSize = 0.7 * eyesSizeY;
        double distToMovePupil = 0.5 * pupilSize;
        double leftEyeLocX = redFace.getLocation().x - gapEyesX - eyesSizeX / 2;
        double rightEyeLocX = redFace.getLocation().x + gapEyesX + eyesSizeX / 2;
        double eyesLocY = redFace.getLocation().y - gapEyesY;

        gc.setLineWidth(2);
        drawOval(gc, leftEyeLocX, eyesLocY, eyesSizeX, eyesSizeY);
        drawOval(gc, rightEyeLocX, eyesLocY, eyesSizeX, eyesSizeY);

        //Draw left eye pupil
        Point2D leftEyePupilLoc = redFace.getDirection() == -1 ? new Point2D(leftEyeLocX, eyesLocY)
                : Utils.moveToDirection(leftEyeLocX, eyesLocY, distToMovePupil, redFace.getDirection());
        double adjustedLeftPupilLocX = Math.max(leftEyeLocX - eyesSizeX / 2 + pupilSize / 2, leftEyePupilLoc.x);
        double adjustedLeftPupilLocY = Math.max(eyesLocY - eyesSizeY / 2 + pupilSize / 2, leftEyePupilLoc.y);
        adjustedLeftPupilLocY = Math.min(eyesLocY + eyesSizeY / 2 - pupilSize / 2, adjustedLeftPupilLocY);
        drawFillOval(gc, adjustedLeftPupilLocX, adjustedLeftPupilLocY, pupilSize, pupilSize);

        //Draw right eye pupil
        Point2D rightEyePupilLoc = redFace.getDirection() == -1 ? new Point2D(rightEyeLocX, eyesLocY)
                : Utils.moveToDirection(rightEyeLocX, eyesLocY, distToMovePupil, redFace.getDirection());
        double adjustedRightPupilLocX = Math.max(rightEyeLocX - eyesSizeX / 2 + pupilSize / 2, rightEyePupilLoc.x);
        double adjustedRightPupilLocY = Math.max(eyesLocY - eyesSizeY / 2 + pupilSize / 2, rightEyePupilLoc.y);
        adjustedRightPupilLocY = Math.min(eyesLocY + eyesSizeY / 2 - pupilSize / 2, adjustedRightPupilLocY);
        drawFillOval(gc, adjustedRightPupilLocX, adjustedRightPupilLocY, pupilSize, pupilSize);
    }

    public static void drawOval(GC gc, double locX, double locY, double sizeX, double sizeY) {
        gc.drawOval((int) (locX - sizeX / 2),
                (int) (locY - sizeY / 2),
                (int) sizeX,
                (int) sizeY
        );
    }

    public static void drawFillOval(GC gc, double locX, double locY, double sizeX, double sizeY) {
        gc.fillOval((int) (locX - sizeX / 2),
                (int) (locY - sizeY / 2),
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
