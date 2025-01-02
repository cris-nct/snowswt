package org.herbshouse.logic.redface;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnemyGenerator extends Thread implements GeneratorListener<AbstractEnemy> {
    private static final int SIZE = 150;
    private static final RGB REMOVE_BACKGROUND_COLOR = new RGB(255, 255, 255);

    private RedFace redFace;
    private AnimatedGif angryFace;
    private AnimatedGif fireGif;
    private final List<AbstractEnemy> enemies = new CopyOnWriteArrayList<>();
    private FlagsConfiguration flagsConfiguration;
    private Rectangle screenBounds;
    private boolean shutdown = false;
    private Point2D mouseLocation;

    public void run() {
        while (!shutdown) {
            this.move();
            Utils.sleep(10);
        }
    }

    private void move() {
        if (Utils.distance(redFace.getLocation(), mouseLocation) > 10) {
            redFace.setDirection(Utils.angleOfPath(redFace.getLocation(), mouseLocation));
            Point2D newLocation = Utils.moveToDirection(redFace.getLocation(), redFace.getSpeed(), redFace.getDirection());
            redFace.setLocation(newLocation);
            if (fireGif != null){
                enemies.remove(fireGif);
            }
            fireGif = null;
        } else {
            redFace.setDirection(-1);
            redFace.update();
            if (fireGif == null){
                fireGif = new AnimatedGif("fire-flame.gif", 1, null);
                fireGif.setLocation(new Point2D(redFace.getLocation().x, redFace.getLocation().y + 25));
                fireGif.setSize(100);
                enemies.add(fireGif);
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
        this.redFace.setLocation(new Point2D(screenBounds.width - 2 * SIZE / 2.0d, screenBounds.height - SIZE / 2.0d));
    }

    @Override
    public void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        this.flagsConfiguration = flagsConfiguration;
        this.screenBounds = screenBounds;
        this.mouseLocation = new Point2D(screenBounds.width / 2.0d, screenBounds.height / 2.0d);

        this.redFace = new RedFace();
        this.redFace.setLocation(new Point2D(screenBounds.width - 2 * SIZE, screenBounds.height - SIZE / 2.0d));
        this.redFace.setSize(SIZE);
        this.redFace.setColor(new RGB(180, 0, 0));
        this.redFace.setSpeed(1);
        this.enemies.add(redFace);

        this.angryFace = new AnimatedGif("angry1.gif", 0.1, REMOVE_BACKGROUND_COLOR);
        this.angryFace.setLocation(new Point2D(screenBounds.width - SIZE / 2.0d, screenBounds.height - SIZE / 2.0d));
        this.angryFace.setSize(SIZE);
        this.angryFace.setSpeed(3);
        this.enemies.add(angryFace);
    }

    @Override
    public List<AbstractEnemy> getMoveableObjects() {
        return enemies;
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public void checkCollisions(ImageData imageData) {

    }
}
