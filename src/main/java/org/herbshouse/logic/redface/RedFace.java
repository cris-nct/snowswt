package org.herbshouse.logic.redface;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class RedFace extends AbstractEnemy {
    private double direction;

    private Point2D eyesSize;

    private double pupilSize;

    private double distToMovePupil;

    private Point2D leftEyeLocation;

    private Point2D rightEyeLocation;

    private Point2D leftPupilLocation;

    private Point2D rightPupilLocation;

    @Override
    public void setSize(int size) {
        super.setSize(size);
        update();
    }

    @Override
    public void setLocation(Point2D location) {
        super.setLocation(location);
        update();
    }

    public void update() {
        double gapEyesX = 0.15 * this.getSize();
        double gapEyesY = 0.2 * this.getSize();
        eyesSize = new Point2D();
        eyesSize.x = 0.2 * this.getSize();
        eyesSize.y = 0.7 * eyesSize.x;
        pupilSize = 0.7 * eyesSize.y;
        distToMovePupil = 0.5 * pupilSize;
        leftEyeLocation = new Point2D(
                this.getLocation().x - gapEyesX - eyesSize.x / 2,
                this.getLocation().y - gapEyesY
        );
        rightEyeLocation = new Point2D(
                this.getLocation().x + gapEyesX + eyesSize.x / 2,
                this.getLocation().y - gapEyesY
        );
        updateLeftPupil();
        updateRightPupil();
    }

    private void updateLeftPupil() {
        Point2D leftEyePupilLoc = this.getDirection() == -1 ? leftEyeLocation
                : Utils.moveToDirection(leftEyeLocation, distToMovePupil, this.getDirection());
        double locX = Math.max(leftEyeLocation.x - eyesSize.x / 2 + pupilSize / 2, leftEyePupilLoc.x);

        double adjustedLeftPupilLocY = Math.max(leftEyeLocation.y - eyesSize.y / 2 + pupilSize / 2, leftEyePupilLoc.y);
        double locY = Math.min(leftEyeLocation.y + eyesSize.y / 2 - pupilSize / 2, adjustedLeftPupilLocY);

        leftPupilLocation = new Point2D(locX, locY);
    }

    private void updateRightPupil() {
        Point2D rightEyePupilLoc = this.getDirection() == -1 ? rightEyeLocation
                : Utils.moveToDirection(rightEyeLocation, distToMovePupil, this.getDirection());
        double locX = Math.max(rightEyeLocation.x - eyesSize.x / 2 + pupilSize / 2, rightEyePupilLoc.x);

        double adjustedRightPupilLocY = Math.max(rightEyeLocation.y - eyesSize.y / 2 + pupilSize / 2, rightEyePupilLoc.y);
        double locY = Math.min(rightEyeLocation.y + eyesSize.y / 2 - pupilSize / 2, adjustedRightPupilLocY);

        rightPupilLocation = new Point2D(locX, locY);
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getDirection() {
        return direction;
    }

    public Point2D getEyesSize() {
        return eyesSize;
    }

    public double getPupilSize() {
        return pupilSize;
    }

    public Point2D getLeftEyeLoc() {
        return leftEyeLocation;
    }

    public Point2D getRightEyeLoc() {
        return rightEyeLocation;
    }

    public Point2D getLeftPupilLoc() {
        return leftPupilLocation;
    }

    public Point2D getRightPupilLoc() {
        return rightPupilLocation;
    }

}
