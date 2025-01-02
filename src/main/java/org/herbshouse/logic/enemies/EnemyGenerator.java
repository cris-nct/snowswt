package org.herbshouse.logic.enemies;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.UserInfo;
import org.herbshouse.logic.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnemyGenerator extends AbstractGenerator<AbstractEnemy> {
    private static final int ANGRY_FACE_SIZE = 150;
    private static final RGB REMOVE_BACKGROUND_COLOR = new RGB(255, 255, 255);
    public static final RGB RED_COLOR = new RGB(160, 0, 0);
    public static final RGB INACTIVE_COLOR = new RGB(150, 150, 150);
    public static final RGB FREE_MOVE_COLOR = new RGB(50, 180, 180);

    private final List<RedFace> redFaces = new CopyOnWriteArrayList<>();
    private final List<AnimatedGif> fireGifs = new CopyOnWriteArrayList<>();
    private AnimatedGif angryFace;
    private FlagsConfiguration flagsConfiguration;
    private Rectangle screenBounds;
    private boolean shutdown = false;
    private long counterFrames;
    private final UserInfo userInfo;

    public EnemyGenerator(UserInfo userInfo) {
        super();
        this.userInfo = userInfo;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        while (!shutdown) {
            if (flagsConfiguration.isEnemies()) {
                if (System.currentTimeMillis() - startTime > 1000) {
                    startTime = System.currentTimeMillis();
                    for (RedFace redFace : redFaces) {
                        redFace.checkTimers();
                        if (flagsConfiguration.isAttack()) {
                            redFace.setState(RedFaceState.FOLLOW, -1);
                        }
                        if (redFace.getKissingGif() == null) {
                            redFace.decreaseLife(1);
                        } else {
                            switch (redFace.getState()) {
                                case FOLLOW -> userInfo.decreasePoints(1);
                                case FREE -> userInfo.increasePoints(1);
                            }
                        }
                    }
                }
                this.move();
                Utils.sleep(10);
            } else {
                redFaces.clear();
                fireGifs.clear();
                Utils.sleep(1000);
            }
        }
    }

    private void move() {
        counterFrames++;
        if (counterFrames % 1000 == 0) {
            generateRedFace();
        }
        for (RedFace redFace : redFaces) {
            if (redFace.getLife() == 0) {
                substractAreaFromShell(
                        Utils.generateCircle(redFace.getLocation(), redFace.getSize() / 2.0d, redFace.getSize(), 0)
                );
                redFaces.remove(redFace);
                continue;
            }
            if (redFace.getKissingGif() == null
                    && Utils.distance(redFace.getLocation(), flagsConfiguration.getMouseLoc()) > redFace.getSize() / 2.0d) {
                switch (redFace.getState()) {
                    case FOLLOW -> {
                        redFace.setDirection(Utils.angleOfPath(redFace.getLocation(), flagsConfiguration.getMouseLoc()));
                        redFace.move();
                    }
                    case FREE -> {
                        redFace.move();
                    }
                }
            } else {
                if (redFace.getState() != RedFaceState.WAITING) {
                    redFace.startKissing();
                }
            }
        }
        for (AnimatedGif fire : fireGifs) {
            fire.setLocation(new Point2D(fire.getLocation().x, fire.getLocation().y - fire.getSpeed()));
            if (fire.getLocation().y < 0) {
                fireGifs.remove(fire);
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
        for (RedFace redFace : redFaces) {
            redFace.stopKissing();
        }
    }

    @Override
    public void mouseDown(int button, Point2D mouseLocation) {
        if (button == 1 && flagsConfiguration.isEnemies()) {
            AnimatedGif gif = new AnimatedGif("fire-flame.gif", 2, null);
            gif.setLocation(mouseLocation);
            gif.setSize(50);
            gif.setSpeed(10);
            fireGifs.add(gif);
        }
    }

    @Override
    public void mouseScrolled(MouseEvent mouseEvent) {

    }

    @Override
    public void reset() {
        this.redFaces.clear();
        this.generateRedFace();
        resetShellSurface();
    }

    @Override
    public void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        this.flagsConfiguration = flagsConfiguration;
        this.screenBounds = screenBounds;

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
        redFace.setLife((int) Utils.linearInterpolation(size, 50, 30, 150, 120));
        redFace.setSpeed(3);
        redFace.setDirection(Math.toRadians(190));
        redFace.setState(RedFaceState.FREE, 10000);
        redFaces.add(redFace);
        return redFace;
    }

    @Override
    public List<AbstractEnemy> getMoveableObjects() {
        List<AbstractEnemy> enemies = new ArrayList<>();
        for (RedFace redFace : redFaces) {
            enemies.add(redFace);
            if (redFace.getKissingGif() != null) {
                enemies.add(redFace.getKissingGif());
            }
        }
        if (angryFace != null && flagsConfiguration.isEnemies()) {
            enemies.add(angryFace);
        }
        enemies.addAll(fireGifs);
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
            this.checkAngryFaceCollision(redFace1);
            this.checkFireCollision(redFace1);
            for (int j = i + 1; j < redFaces.size(); j++) {
                RedFace redFace2 = redFaces.get(j);
                if (isCollide(redFace1, redFace2)) {
                    if (redFace2.getKissingGif() != null || redFace1.getKissingGif() != null) {
                        if (redFace1.getState() != RedFaceState.WAITING) {
                            redFace1.startKissing();
                        }
                        if (redFace2.getState() != RedFaceState.WAITING) {
                            redFace2.startKissing();
                        }
                    } else {
                        redFace1.setDirection(redFace1.getDirection() + Math.PI);
                        redFace1.setState(RedFaceState.WAITING, 1000 + (int) (Math.random() * 5000));
                    }
                }
            }
        }
    }

    private void checkFireCollision(RedFace redFace) {
        for (AnimatedGif fire : fireGifs) {
            if (isCollide(redFace, fire)) {
                if (redFace.getState() == RedFaceState.FREE || redFace.getState() == RedFaceState.WAITING) {
                    redFace.decreaseLife(20);
                    if (redFace.getLife() == 0) {
                        redFaces.remove(redFace);
                    }
                } else {
                    redFace.setState(RedFaceState.FREE, 5000);
                }
                fireGifs.remove(fire);
                break;
            }
        }
    }

    private void checkAngryFaceCollision(RedFace redFace) {
        if (isCollide(redFace, angryFace)) {
            redFace.increaseLife(10);
        }
    }

    private boolean isCollide(AbstractEnemy redFace1, AbstractEnemy redFace2) {
        int sumSize = (redFace1.getSize() + redFace2.getSize()) / 2 + 5;
        return Utils.distance(redFace1.getLocation(), redFace2.getLocation()) < sumSize;
    }

    private void checkWallCollision(RedFace redFace) {
        double errFactor = 10;
        // Check collision with left and right walls
        if (redFace.getLocation().x - redFace.getSize() / 2.0d <= screenBounds.x - 1) {
            if (redFace.getLocation().y <= screenBounds.y + screenBounds.height / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            }
            redFace.setLocation(new Point2D(screenBounds.x + redFace.getSize() / 2.0d + errFactor, redFace.getLocation().y));
        } else if (redFace.getLocation().x + redFace.getSize() / 2.0d >= screenBounds.x + screenBounds.width) {
            if (redFace.getLocation().y <= screenBounds.y + screenBounds.height / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            }
            redFace.setLocation(new Point2D(screenBounds.x + screenBounds.width - redFace.getSize() / 2.0d - errFactor, redFace.getLocation().y));
        }

        if (redFace.getLocation().y - redFace.getSize() / 2.0d <= screenBounds.y) {
            if (redFace.getLocation().x <= screenBounds.x + screenBounds.width / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            }
            redFace.setLocation(new Point2D(redFace.getLocation().x, screenBounds.y + redFace.getSize() / 2.0d + errFactor));
        } else if (redFace.getLocation().y + redFace.getSize() / 2.0d >= screenBounds.y + screenBounds.height) {
            if (redFace.getLocation().x <= screenBounds.x + screenBounds.width / 2.0d) {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() - Math.PI / 2));
            } else {
                redFace.setDirection(Utils.normAngle(redFace.getDirection() + Math.PI / 2));
            }
            redFace.setLocation(new Point2D(redFace.getLocation().x, screenBounds.y + screenBounds.height - redFace.getSize() / 2.0d - errFactor));
        }
    }
}
