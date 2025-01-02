package org.herbshouse.logic.redface;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnemyGenerator extends Thread implements GeneratorListener<AbstractEnemy> {
    private static final int ANGRY_FACE_SIZE = 150;
    private static final RGB REMOVE_BACKGROUND_COLOR = new RGB(255, 255, 255);
    public static final RGB RED_COLOR = new RGB(180, 0, 0);
    public static final RGB INACTIVE_COLOR = new RGB(150, 150, 150);
    public static final RGB FREE_MOVE_COLOR = new RGB(50, 180, 180);

    private final List<RedFace> redFaces = new CopyOnWriteArrayList<>();
    private AnimatedGif angryFace;
    private AnimatedGif fireGif;
    private FlagsConfiguration flagsConfiguration;
    private Rectangle screenBounds;
    private boolean shutdown = false;
    private Point2D mouseLocation;
    private long counterFrames;

    public void run() {
        while (!shutdown) {
            this.move();
            Utils.sleep(10);
        }
    }

    private void move() {
        counterFrames++;
        if (counterFrames % 1000 == 0) {
            generateRedFace();
        }
        for (RedFace redFace : redFaces) {
            if (Utils.distance(redFace.getLocation(), mouseLocation) > 10) {
                if (!redFace.isFreeMovement()) {
                    redFace.setDirection(Utils.angleOfPath(redFace.getLocation(), mouseLocation));
                }
                if (redFace.isWaiting()) {
                    redFace.setColor(INACTIVE_COLOR);
                } else if (redFace.isFreeMovement()) {
                    redFace.setColor(FREE_MOVE_COLOR);
                    redFace.move(0);
                } else {
                    redFace.setColor(RED_COLOR);
                    redFace.move(0);
                }
                fireGif = null;
            } else {
                redFace.setDirection(-1);
                if (fireGif == null) {
                    fireGif = new AnimatedGif("fire-flame.gif", 1, null);
                    fireGif.setLocation(new Point2D(redFace.getLocation().x, redFace.getLocation().y + 25));
                    fireGif.setSize(100);
                }
            }
        }
    }

    @Override
    public void turnOnHappyWind() {

    }

    @Override
    public void turnOffHappyWind() {

    }

    @Override
    public void freezeMovableObjects() {

    }

    @Override
    public void switchDebug() {

    }

    @Override
    public void mouseMove(Point2D mouseLocation) {
        this.mouseLocation = mouseLocation;
    }

    @Override
    public void mouseDown(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseScrolled(MouseEvent mouseEvent) {

    }

    @Override
    public void reset() {
        this.redFaces.clear();
        this.generateRedFace();
    }

    @Override
    public void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        this.flagsConfiguration = flagsConfiguration;
        this.screenBounds = screenBounds;
        this.mouseLocation = new Point2D(screenBounds.width / 2.0d, screenBounds.height / 2.0d);

        this.generateRedFace();

        this.angryFace = new AnimatedGif("angry1.gif", 0.3, REMOVE_BACKGROUND_COLOR);
        this.angryFace.setLocation(new Point2D(screenBounds.width - ANGRY_FACE_SIZE / 2.0d, screenBounds.height - ANGRY_FACE_SIZE / 2.0d));
        this.angryFace.setSize(ANGRY_FACE_SIZE);
        this.angryFace.setSpeed(3);
    }

    private RedFace generateRedFace() {
        RedFace redFace = new RedFace();
        int size = 50 + (int) (Math.random() * 100);
        redFace.setLocation(new Point2D(screenBounds.width - size - 10, screenBounds.height - size - 10));
        redFace.setSize(size);
        redFace.setColor(RED_COLOR);
        redFace.setSpeed(3);
        redFace.setDirection(Math.toRadians(190));
        redFace.freeMovementFor(10000);
        redFaces.add(redFace);
        return redFace;
    }

    @Override
    public List<AbstractEnemy> getMoveableObjects() {
        List<AbstractEnemy> enemies = new ArrayList<>(redFaces);
        if (angryFace != null) {
            enemies.add(angryFace);
        }
        if (fireGif != null) {
            enemies.add(fireGif);
        }
        return enemies;
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public void checkCollisions(ImageData imageData) {
        for (int i = 0; i < redFaces.size(); i++) {
            //Check collision with walls
            RedFace redFace1 = redFaces.get(i);
            this.checkWallCollision(redFace1);
            for (int j = i + 1; j < redFaces.size(); j++) {
                RedFace redFace2 = redFaces.get(j);
                if (isCollide(redFace1, redFace2)) {
                    if (!redFace1.isFreeMovement()) {
                        redFace1.stopFor(1000 + (int) (Math.random() * 5000));
                    }
                    redFace2.freeMovementFor(5000);
                }
            }
        }
    }

    private boolean isCollide(RedFace redFace1, RedFace redFace2) {
        int sumSize = (redFace1.getSize() + redFace2.getSize()) / 2 + 5;
        return Utils.distance(redFace1.getLocation(), redFace2.getLocation()) < sumSize;
    }

    private void checkWallCollision(RedFace redFace) {
        double errFactor = 10;
        boolean wallCollision = false;
        // Check collision with left and right walls
        if (redFace.getLocation().x - redFace.getSize() / 2.0d <= screenBounds.x - 1) {
            if (redFace.getLocation().y <= screenBounds.y + screenBounds.height / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            }
            redFace.setLocation(new Point2D(screenBounds.x + redFace.getSize() / 2.0d + errFactor, redFace.getLocation().y));
            wallCollision = true;
        } else if (redFace.getLocation().x + redFace.getSize() / 2.0d >= screenBounds.x + screenBounds.width) {
            if (redFace.getLocation().y <= screenBounds.y + screenBounds.height / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            }
            redFace.setLocation(new Point2D(screenBounds.x + screenBounds.width - redFace.getSize() / 2.0d - errFactor, redFace.getLocation().y));
            wallCollision = true;
        }

        if (redFace.getLocation().y - redFace.getSize() / 2.0d <= screenBounds.y) {
            if (redFace.getLocation().x <= screenBounds.x + screenBounds.width / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            }
            redFace.setLocation(new Point2D(redFace.getLocation().x, screenBounds.y + redFace.getSize() / 2.0d + errFactor));
            wallCollision = true;
        } else if (redFace.getLocation().y + redFace.getSize() / 2.0d >= screenBounds.y + screenBounds.height) {
            if (redFace.getLocation().x <= screenBounds.x + screenBounds.width / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            }
            redFace.setLocation(new Point2D(redFace.getLocation().x, screenBounds.y + screenBounds.height - redFace.getSize() / 2.0d - errFactor));
            wallCollision = true;
        }

        if (wallCollision) {
            redFace.freeMovementFor(5000);
        }
    }
}
