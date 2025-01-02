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

    private long timeStopFor = 0;

    private int stopFor = 0;

    private long timeFreeMovement = 0;

    private int freeMovementFor = 0;

    private int life = 60;

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

    public void setDirection(double direction) {
        this.direction = direction;
        update();
    }

    public void move() {
        this.setLocation(Utils.moveToDirection(getLocation(), getSpeed(), direction));
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

    public void stopFor(int milliseconds) {
        this.stopFor = milliseconds;
        this.timeStopFor = System.currentTimeMillis();
    }

    public void freeMovementFor(int milliseconds) {
        this.freeMovementFor = milliseconds;
        this.timeFreeMovement = System.currentTimeMillis();
    }

    public boolean isWaiting(){
        boolean waiting = false;
        if (stopFor > 0){
            waiting = (System.currentTimeMillis() - timeStopFor < stopFor);
            if (!waiting){
                stopFor = 0;
                timeStopFor = 0;
                freeMovementFor(5000);
            }
        }
        return waiting;
    }

    public boolean isFreeMovement(){
        boolean freeMove = false;
        if (freeMovementFor > 0){
            freeMove = (System.currentTimeMillis() - timeFreeMovement < freeMovementFor);
            if (!freeMove){
                freeMovementFor = 0;
                timeFreeMovement = 0;
            }
        }
        return freeMove;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void increaseLife(int life) {
        this.life += life;
    }

    public void decreaseLife(int life) {
        this.life = Math.max(this.life - life, 0);
    }
}
