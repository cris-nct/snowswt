package org.herbshouse.logic.snow;


import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SnowGenerator extends AbstractGenerator<Snowflake> {

    private final List<Snowflake> snowflakes = new CopyOnWriteArrayList<>();
    private final List<Snowflake> toRemove = new ArrayList<>();
    private final ReentrantLock lockSnowflakes = new ReentrantLock(false);
    private Rectangle drawingSurface;
    private boolean shutdown = false;
    private FlagsConfiguration flagsConfiguration;
    private int counterUpdates;
    private int countdown;
    private ImageData imageData;

    @Override
    public List<Snowflake> getMoveableObjects() {
        return snowflakes;
    }

    @Override
    public void run() {
        this.initialAnimation();
        while (!shutdown) {
            if (lockSnowflakes.tryLock()) {
                this.update();
                if (!flagsConfiguration.isAttack()) {
                    this.checkCollisions();
                }
                lockSnowflakes.unlock();
            }
            if (flagsConfiguration.getSnowingLevel() == 0) {
                Utils.sleep(FlagsConfiguration.SLEEP_GENERATOR_DOING_NOTHING);
            } else {
                Utils.sleep(FlagsConfiguration.SLEEP_SNOWFLAKE_GENERATOR);
            }
        }
    }

    private void update() {
        if (!flagsConfiguration.isDebug() && !flagsConfiguration.isAttack()) {
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
        Snowflake prevSnowFlake = null;
        for (Snowflake snowflake : snowflakes) {
            if (snowflake.isFreezed()) {
                continue;
            }
            this.move(snowflake, prevSnowFlake);
            if (snowflake.getLocation().y > drawingSurface.height) {
                toRemove.add(snowflake);
            } else {
                prevSnowFlake = snowflake;
            }
        }
        if (toRemove.size() > 100) {
            snowflakes.removeAll(toRemove);
            toRemove.clear();
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

    private void move(Snowflake snowflake, Snowflake prevSnowFlake) {
        if (flagsConfiguration.isDebug()) {
            snowflake.registerHistoryLocation();
        }
        if (flagsConfiguration.isAttack() && flagsConfiguration.getAttackType() == 1) {
            this.moveSnowflakeAttack1(snowflake, prevSnowFlake);
        } else if (flagsConfiguration.isAttack() && flagsConfiguration.getAttackType() == 2) {
            this.moveSnowflakeAttack2(snowflake);
        } else if (flagsConfiguration.isHappyWind()) {
            this.moveSnowflakeHappyWind(snowflake);
        } else if (flagsConfiguration.isNormalWind()) {
            this.moveSnowflakeNormalWind(snowflake);
        } else {
            this.regularSnowing(snowflake);
        }
    }

    private void regularSnowing(Snowflake snowflake) {
        Point2D newLoc = snowflake.getLocation().clone();
        newLoc.x = Math.min(newLoc.x, drawingSurface.width);
        newLoc.y += snowflake.getSpeed();
        snowflake.setLocation(newLoc);
    }

    private void moveSnowflakeNormalWind(Snowflake snowflake) {
        Point2D newLoc = snowflake.getLocation().clone();
        int startCriticalArea = drawingSurface.height / 4;
        int endCriticalArea = startCriticalArea + 100;
        if (newLoc.y > startCriticalArea && newLoc.y < endCriticalArea) {
            newLoc.x += Utils.linearInterpolation(newLoc.x, 1, 4, drawingSurface.width, 0);
        } else if (newLoc.y > endCriticalArea) {
            //noinspection SuspiciousNameCombination
            newLoc.x += Utils.linearInterpolation(newLoc.y, endCriticalArea, 2, drawingSurface.height, 0);
        }
        newLoc.x = Math.min(newLoc.x, drawingSurface.width);
        newLoc.y += snowflake.getSpeed();
        snowflake.setLocation(newLoc);
    }

    private void moveSnowflakeHappyWind(Snowflake snowflake) {
        Point2D newLoc = snowflake.getLocation().clone();
        HappyWindSnowFlakeData data = snowflake.getHappyWindData();
        newLoc.x = data.getOrigLocation().x + data.getAreaToMove() * Math.sin(data.getAngle());
        data.increaseAngle();
        newLoc.x = Math.min(newLoc.x, drawingSurface.width);
        newLoc.y += snowflake.getSpeed();
        snowflake.setLocation(newLoc);
    }

    private void moveSnowflakeAttack1(Snowflake snowflake, Snowflake prevSnowFlake) {
        double directionToTarget;
        boolean move = true;
        if (prevSnowFlake == null) {
            directionToTarget = Utils.angleOfPath(snowflake.getLocation(), flagsConfiguration.getMouseLoc());
        } else {
            directionToTarget = Utils.angleOfPath(snowflake.getLocation(), prevSnowFlake.getLocation());
            if (isColliding(snowflake, prevSnowFlake)) {
                move = false;
            }
        }
        if (move) {
            double distance = Math.abs(Math.sin(Math.toRadians(snowflake.getAttackData().getCounter())));
            Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), distance, directionToTarget);
            snowflake.setLocation(newLoc);
        }
    }

    private void moveSnowflakeAttack2(Snowflake snowflake) {
        Point2D dest = snowflake.getAttackData().getLocationToFollow();
        if (dest == null) {
            dest = Utils.moveToDirection(flagsConfiguration.getMouseLoc(), 250, Math.toRadians(Math.random() * 360));
            snowflake.getAttackData().setLocationToFollow(dest);
        }
        double directionToTarget = Utils.angleOfPath(snowflake.getLocation(), dest);
        Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), 1, directionToTarget);
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
        HappyWindSnowFlakeData data = snowflake.getHappyWindData();
        data.setOrigLocation(snowflake.getLocation().clone());
        int maxAreaToMove = 50;
        if (flagsConfiguration.isDebug()) {
            data.setAngleIncrease(Math.toRadians(5));
            data.setAreaToMove(50);
        } else {
            data.setAngleIncrease(2 * Math.random() / 100);
            data.setAreaToMove(Math.abs((int) (Math.random() * maxAreaToMove)));
        }
        snowflake.setSpeed(Utils.linearInterpolation(data.getAreaToMove(), maxAreaToMove, 2, 1, 0.5));
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
        try {
            if (flagsConfiguration.isAttack()) {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    for (Snowflake snowflake : snowflakes) {
                        snowflake.getAttackData().setLocationToFollow(null);
                    }
                    lockSnowflakes.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
            if (countdown == -1) {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    snowflakes.clear();
                    lockSnowflakes.unlock();
                }
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
    public void mouseDown(int button, Point2D mouseLocation) {
        if (button == 3) {
            try {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    Snowflake snowflake = generateNewSnowflake(50, 1);
                    snowflake.setLocation(mouseLocation);
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
    public void turnOffSnowing() {
        try {
            if (countdown == -1) {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    snowflakes.clear();
                    lockSnowflakes.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void provideImageData(ImageData imageData) {
        this.imageData = imageData;
    }

    private void checkCollisions() {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                for (Snowflake snowflake : snowflakes) {
                    if (!snowflake.isFreezed()
                            && snowflake.getLocation().x > drawingSurface.width / 2 - 150
                            && snowflake.getLocation().x < drawingSurface.width / 2 + 150
                            && this.isColliding(snowflake, imageData)) {
                        snowflake.freeze();
                    }
                }
                lockSnowflakes.unlock();
            }
        } catch (
                InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isColliding(Snowflake snowflake, ImageData imageData) {
        final Set<RGB> colors = new HashSet<>();
        for (int x = -1; x < 2; x++) {
            RGB pixelColorBottom = GuiUtils.getPixelColor(
                    imageData,
                    (int) snowflake.getLocation().x + x,
                    (int) snowflake.getLocation().y
            );
            colors.add(pixelColorBottom);
        }
        return colors.stream().anyMatch(p -> p.equals(GuiUtils.FREEZE_COLOR));
    }

}
