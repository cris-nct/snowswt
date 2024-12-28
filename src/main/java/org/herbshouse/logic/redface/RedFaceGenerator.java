package org.herbshouse.logic.redface;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.SnowListener;
import org.herbshouse.logic.Utils;

import java.util.List;

public class RedFaceGenerator extends Thread implements SnowListener<RedFace> {
    private static final int SIZE = 150;

    private RedFace redFace;
    private FlagsConfiguration flagsConfiguration;
    private Rectangle screenBounds;
    private boolean shutdown = false;

    public void run() {
        while (!shutdown) {
            this.move();
            Utils.sleep(10);
        }
    }

    private void move() {

    }

    @Override
    public void turnOnHappyWind() {

    }

    @Override
    public void turnOffHappyWind() {

    }

    @Override
    public void freezeSnowflakes() {

    }

    @Override
    public void switchDebug() {

    }

    @Override
    public void mouseMove(Point2D mouseLocation) {

    }

    @Override
    public void mouseDown(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseScrolled(MouseEvent mouseEvent) {

    }

    @Override
    public void reset() {
        redFace.setLocation(new Point2D(screenBounds.width - SIZE/2.0d, screenBounds.height - SIZE/2.0d));
    }

    @Override
    public void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
        this.flagsConfiguration = flagsConfiguration;
        this.screenBounds = screenBounds;
        redFace = new RedFace();
        redFace.setSize(SIZE);
        redFace.setColor(new RGB(180, 0, 0));
        redFace.setSpeed(1);
        redFace.setLocation(new Point2D(screenBounds.width - SIZE/2.0d, screenBounds.height - SIZE/2.0d));
    }

    @Override
    public List<RedFace> getSnowflakes() {
        return List.of(redFace);
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public void checkCollisions(ImageData imageData) {

    }
}
