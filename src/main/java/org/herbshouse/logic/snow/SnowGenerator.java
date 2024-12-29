package org.herbshouse.logic.snow;


import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SnowGenerator extends Thread implements GeneratorListener<Snowflake> {

    private final List<Snowflake> snowflakes = new CopyOnWriteArrayList<>();
    private final List<Snowflake> toRemove = new ArrayList<>();
    private final Map<Snowflake, HappyWindSnowFlakeData> happyWindSnowData = new HashMap<>();
    private final ReentrantLock lockSnowflakes = new ReentrantLock(false);
    private Rectangle drawingSurface;
    private boolean shutdown = false;
    private FlagsConfiguration flagsConfiguration;
    private Point2D mouseLocation;
    private int counterUpdates;
    private int countdown;

    @Override
    public List<Snowflake> getMoveableObjects() {
        return snowflakes;
    }

    @Override
    public void run() {
        this.initialAnimation();
        while (!shutdown) {
            if (lockSnowflakes.tryLock()) {
                if (!flagsConfiguration.isDebug()) {
                    if (flagsConfiguration.getSnowingLevel() > 0) {
                        for (int i = 0; i < flagsConfiguration.getSnowingLevel(); i++) {
                            this.generateNewSnowflake();
                        }
                    } else if (flagsConfiguration.getSnowingLevel() < 0) {
                        counterUpdates++;
                        if (counterUpdates >= -flagsConfiguration.getSnowingLevel()) {
                            this.generateNewSnowflake();
                            counterUpdates = 0;
                        }
                    }
                }

                //Move all snowflakes
                for (Snowflake snowflake : snowflakes) {
                    if (snowflake.isFreezed()) {
                        continue;
                    }
                    this.move(snowflake);
                    if (snowflake.getLocation().y > drawingSurface.height) {
                        toRemove.add(snowflake);
                    }
                }
                if (toRemove.size() > 100) {
                    if (flagsConfiguration.isHappyWind()) {
                        toRemove.forEach(happyWindSnowData::remove);
                    }
                    snowflakes.removeAll(toRemove);
                    toRemove.clear();
                }
                lockSnowflakes.unlock();
            }

            Utils.sleep(5);
        }
    }

    private void initialAnimation() {
        //noinspection IntegerDivisionInFloatingPointContext
        Point2D location = new Point2D(drawingSurface.width / 2, drawingSurface.height / 2);
        int numberOfFlakes = 50;
        for (int k = 0; k < numberOfFlakes; k++) {
            Snowflake snowflake = new Snowflake();
            snowflake.setLocation(location);
            snowflake.setSize(15);
            snowflake.setColor(new RGB(240, 0, 0));
            snowflake.setAlpha((int) Utils.linearInterpolation(k, 0, 50, numberOfFlakes - 1, 255));
            snowflakes.add(snowflake);
        }
        for (countdown = 10; countdown > 0; countdown--) {
            for (double angle = 0; angle < 360; angle++) {
                for (int k = 0; k < numberOfFlakes; k++) {
                    snowflakes.get(k).setLocation(Utils.moveToDirection(location, 200, Math.toRadians(angle + k * 2)));
                }
                Utils.sleep(3);
            }
        }
        countdown = -1;
        snowflakes.clear();
    }

    @Override
    public int getCountdown() {
        return countdown;
    }

    private void move(Snowflake snowflake) {
        if (flagsConfiguration.isDebug()) {
            snowflake.registerHistoryLocation();
        }
        Point2D newLoc = snowflake.getLocation().clone();
        if (flagsConfiguration.isAttack() && snowflake.getSize() > 3) {
            double distance = Utils.distance(snowflake.getLocation(), mouseLocation);
            if (distance < 5) {
                snowflake.freeze();
            } else {
                double directionToTarget = Utils.angleOfPath(snowflake.getLocation(), mouseLocation);
                newLoc = Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
                snowflake.setLocation(newLoc);
            }
            return;
        }
        if (flagsConfiguration.isHappyWind()) {
            HappyWindSnowFlakeData data = happyWindSnowData.get(snowflake);
            newLoc.x = data.getOrigLocation().x + data.getAreaToMove() * Math.sin(data.getAngle());
            data.increaseAngle();
        } else if (flagsConfiguration.isNormalWind()) {
            int startCriticalArea = drawingSurface.height / 4;
            int endCriticalArea = startCriticalArea + 100;
            if (newLoc.y > startCriticalArea && newLoc.y < endCriticalArea) {
                newLoc.x += Utils.linearInterpolation(newLoc.x, 1, 4, drawingSurface.width, 0);
            } else if (newLoc.y > endCriticalArea) {
                //noinspection SuspiciousNameCombination
                newLoc.x += Utils.linearInterpolation(newLoc.y, endCriticalArea, 2, drawingSurface.height, 0);
            }
        }

        newLoc.x = Math.min(newLoc.x, drawingSurface.width);
        newLoc.y += snowflake.getSpeed();
        snowflake.setLocation(newLoc);
    }

    private Snowflake generateNewSnowflake() {
        final int size;
        if (flagsConfiguration.isBigBalls()) {
            size = 22 + (int) (Math.random() * 40);
        } else {
            size = 2 + (int) (Math.random() * 6);
        }
        double speed = Math.min(Utils.linearInterpolation(size, 1, 0.3, 4, 1), 1);
        return generateNewSnowflake(size, speed);
    }

    private Snowflake generateNewSnowflake(int size, double speed) {
        final Snowflake snowflake = new Snowflake();
        snowflake.setLocation(new Point2D(Math.random() * drawingSurface.width, 0));
        snowflake.setSize(size);
        snowflake.setSpeed(speed);
        if (flagsConfiguration.isHappyWind()) {
            initializeSnowFlakeHappyWind(snowflake);
        }
        snowflakes.add(snowflake);
        return snowflake;
    }

    @Override
    public void turnOnHappyWind() {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                for (Snowflake snowflake : snowflakes) {
                    initializeSnowFlakeHappyWind(snowflake);
                }
                lockSnowflakes.unlock();
            } else {
                System.out.println("GUI thread is overloaded !!!");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeSnowFlakeHappyWind(Snowflake snowflake) {
        happyWindSnowData.putIfAbsent(snowflake, new HappyWindSnowFlakeData());
        HappyWindSnowFlakeData data = happyWindSnowData.get(snowflake);
        data.setOrigLocation(snowflake.getLocation().clone());
        int maxAreaToMove = 200;
        if (flagsConfiguration.isDebug()) {
            data.setAngleIncrease(0.03);
            data.setAreaToMove(50);
        } else {
            data.setAngleIncrease(2 * Math.random() / 100);
            data.setAreaToMove(Math.abs((int) (Math.random() * maxAreaToMove)));
        }
        snowflake.setSpeed(Utils.linearInterpolation(data.getAreaToMove(), maxAreaToMove, 3, 1, 1));
    }

    @Override
    public void turnOffHappyWind() {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                happyWindSnowData.clear();
                lockSnowflakes.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void freezeMovableObjects() {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                for (Snowflake snowflake : snowflakes) {
                    snowflake.freeze();
                }
                lockSnowflakes.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void switchDebug() {
        if (flagsConfiguration.isDebug()) {
            try {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    snowflakes.clear();
                    happyWindSnowData.clear();
                    this.generateNewSnowflake(20, 0.5);
                    lockSnowflakes.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void mouseMove(Point2D mouseLocation) {
        this.mouseLocation = mouseLocation;
    }

    @Override
    public void mouseScrolled(MouseEvent mouseEvent) {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                for (Snowflake snowflake : snowflakes) {
                    if (!snowflake.isFreezed()) {
                        Point2D newLoc = snowflake.getLocation();
                        newLoc.y += 10 * mouseEvent.count;
                        snowflake.setLocation(newLoc);
                    }
                }
                lockSnowflakes.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reset() {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                snowflakes.clear();
                happyWindSnowData.clear();
                lockSnowflakes.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(FlagsConfiguration flagsConfiguration, Rectangle drawingSurface) {
        this.flagsConfiguration = flagsConfiguration;
        this.drawingSurface = drawingSurface;
    }

    @Override
    public void mouseDown(MouseEvent mouseEvent) {
        if (mouseEvent.button == 3) {
            try {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    Snowflake snowflake = generateNewSnowflake(50, 1);
                    snowflake.setLocation(new Point2D(mouseEvent.x, mouseEvent.y));
                    snowflake.freeze();
                    lockSnowflakes.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public void checkCollisions(ImageData imageData) {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                for (Snowflake snowflake : snowflakes) {
                    if (isColliding(snowflake, imageData)) {
                        snowflake.freeze();
                    }
                }
                lockSnowflakes.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isColliding(Snowflake snowflake, ImageData imageData) {
        List<RGB> colors = new ArrayList<>();
        RGB pixelColorRight = GuiUtils.getPixelColor(imageData,
                (int) snowflake.getLocation().x + snowflake.getSize() / 2,
                (int) snowflake.getLocation().y
        );
        colors.add(pixelColorRight);

        for (int i = 0; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                RGB pixelColorBottom = GuiUtils.getPixelColor(imageData,
                        (int) snowflake.getLocation().x + j,
                        (int) snowflake.getLocation().y + snowflake.getSize() / 2 + i
                );
                colors.add(pixelColorBottom);
            }
        }

        return colors.stream().anyMatch(p -> p.equals(GuiUtils.FREEZE_COLOR));
    }


}
