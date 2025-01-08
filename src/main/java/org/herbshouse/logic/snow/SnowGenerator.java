package org.herbshouse.logic.snow;


import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.attack.attack2.SnowflakeAttack2Logic;
import org.herbshouse.logic.snow.attack.attack3.SnowflakeAttack3Logic;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SnowGenerator extends AbstractGenerator<Snowflake> {

    private final List<Snowflake> snowflakes = new CopyOnWriteArrayList<>();
    private final List<Snowflake> toRemove = new ArrayList<>();
    private final ReentrantLock lockSnowflakes = new ReentrantLock(false);
    private SnowflakeAttack2Logic attack2Logic;
    private SnowflakeAttack3Logic attack3Logic;
    private final Timer timer;
    private Rectangle screenBounds;
    private boolean shutdown = false;
    private FlagsConfiguration flagsConfiguration;
    private int countdown;
    private ImageData imageData;
    private boolean skipInitialAnimation;
    private int counterFreezeSnowklakes;
    private int snowflakesTimeGen = 100;
    private TimerTask taskSnowing;
    private boolean pauseSnowing;

    public SnowGenerator() {
        timer = new Timer("SnowGeneratorTimer");
    }

    @Override
    public List<Snowflake> getMoveableObjects() {
        return snowflakes;
    }

    @Override
    public void run() {
        if (skipInitialAnimation) {
            countdown = -1;
        } else {
            this.initialAnimation();
        }
        this.resetSnowingTimer();
        while (!shutdown) {
            if (lockSnowflakes.tryLock()) {
                this.update();
                if (!flagsConfiguration.isAttack() && counterFreezeSnowklakes < 3000) {
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
        this.snowflakes.clear();
        attack2Logic.shutdown();
        taskSnowing.cancel();
        timer.cancel();
        timer.purge();
    }

    private void update() {
        //Move all snowflakes
        Snowflake prevSnowFlake = null;
        int snowflakeindex = 0;
        counterFreezeSnowklakes = 0;
        for (Snowflake snowflake : snowflakes) {
            if (snowflake.isFreezed()) {
                counterFreezeSnowklakes++;
                continue;
            }
            this.move(snowflake, prevSnowFlake, snowflakeindex);
            if (snowflake.getLocation().y <= 0 && !flagsConfiguration.isAttack()) {
                toRemove.add(snowflake);
            } else {
                prevSnowFlake = snowflake;
            }
            snowflakeindex++;
        }
        if (toRemove.size() > 100) {
            snowflakes.removeAll(toRemove);
            toRemove.clear();
        }

        if (flagsConfiguration.isAttack()) {
            if (flagsConfiguration.getAttackType() == 2) {
                attack2Logic.postProcessing(snowflakes);
            } else if (flagsConfiguration.getAttackType() == 3) {
                attack3Logic.postProcessing(snowflakes);
            }
        }
    }

    private void initialAnimation() {
        //noinspection IntegerDivisionInFloatingPointContext
        Point2D location = new Point2D(screenBounds.width / 2, screenBounds.height / 2);
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

    private void move(Snowflake snowflake, Snowflake prevSnowFlake, int index) {
        snowflake.getSnowTail().registerHistoryLocation();
        if (flagsConfiguration.isAttack() && flagsConfiguration.getAttackType() == 1) {
            this.moveSnowflakeAttack1(snowflake, prevSnowFlake);
        } else if (flagsConfiguration.isAttack() && flagsConfiguration.getAttackType() == 2) {
            snowflake.setLocation(attack2Logic.computeNextLocation(snowflake));
        } else if (flagsConfiguration.isAttack() && flagsConfiguration.getAttackType() == 3 && index < 20) {
            snowflake.setLocation(attack3Logic.computeNextLocation(snowflake));
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
        newLoc.x = Math.min(newLoc.x, screenBounds.width);
        newLoc.y -= snowflake.getSpeed();
        snowflake.setLocation(newLoc);
    }

    private void moveSnowflakeNormalWind(Snowflake snowflake) {
        Point2D newLoc = snowflake.getLocation().clone();
        int startCriticalArea = screenBounds.height / 2 + 200;
        int endCriticalArea = startCriticalArea + 100;
        if (newLoc.y > startCriticalArea && newLoc.y < endCriticalArea) {
            newLoc.x += Utils.linearInterpolation(newLoc.x, 1, 4, screenBounds.width, 0);
        } else if (newLoc.y < endCriticalArea) {
            //noinspection SuspiciousNameCombination
            newLoc.x += Utils.linearInterpolation(newLoc.y, endCriticalArea, 2, 0, 0);
        }
        newLoc.x = Math.min(newLoc.x, screenBounds.width);
        newLoc.y -= snowflake.getSpeed();
        snowflake.setLocation(newLoc);
    }

    private void moveSnowflakeHappyWind(Snowflake snowflake) {
        Point2D newLoc = snowflake.getLocation().clone();
        HappyWindSnowFlakeData data = snowflake.getHappyWindData();
        newLoc.x = data.getOrigLocation().x + data.getAreaToMove() * Math.sin(data.getAngle());
        data.increaseAngle();
        newLoc.x = Math.min(newLoc.x, screenBounds.width);
        newLoc.y -= snowflake.getSpeed();
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
            double distance = Math.abs(Math.sin(Math.toRadians(snowflake.getAttackData2().getCounterDegrees())));
            Point2D newLoc = Utils.moveToDirection(snowflake.getLocation(), distance, directionToTarget);
            snowflake.setLocation(newLoc);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private Snowflake generateNewSnowflake() {
        final int size;
        if (flagsConfiguration.isBigBalls()) {
            size = 12 + (int) (Math.random() * 20);
        } else {
            size = 2 + (int) (Math.random() * 6);
        }
        final double speed;
        int snowingLevel = flagsConfiguration.getSnowingLevel();
        if (snowingLevel < 3) {
            speed = 0.5 + Math.random();
        } else if (snowingLevel < 6) {
            speed = 0.5 + Math.random() * 2;
        } else {
            speed = 0.7 + Math.random() * 2.5;
        }
        return generateNewSnowflake(size, speed);
    }

    private Snowflake generateNewSnowflake(int size, double speed) {
        final Snowflake snowflake = new Snowflake();
        snowflake.setLocation(new Point2D(Math.random() * screenBounds.width, screenBounds.height));
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
            pauseSnowing = true;
            try {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    snowflakes.clear();
                    this.generateNewSnowflake(20, 0.5);
                    lockSnowflakes.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            pauseSnowing = false;
        }
    }

    @Override
    public void mouseMove(Point2D mouseLocation) {
    }

    @Override
    public void mouseScrolled(int count) {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                for (Snowflake snowflake : snowflakes) {
                    if (!snowflake.isFreezed()) {
                        Point2D newLoc = snowflake.getLocation();
                        newLoc.y += 10 * count;
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
        this.screenBounds = drawingSurface;
        this.attack2Logic = new SnowflakeAttack2Logic(flagsConfiguration, screenBounds);
        this.attack3Logic = new SnowflakeAttack3Logic(flagsConfiguration, screenBounds);
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
    public void switchAttack() {
        if (flagsConfiguration.isAttack()) {
            pauseSnowing = true;
            try {
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    snowflakes.removeAll(snowflakes.stream().filter(Snowflake::isFreezed).toList());
                    attack2Logic.updateIncrementsBounds(flagsConfiguration.getSnowingLevel());
                    lockSnowflakes.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            attack2Logic.resetTimers();
            pauseSnowing = false;
        }
    }

    @Override
    public void changedSnowingLevel() {
        try {
            if (countdown == -1 && flagsConfiguration.getSnowingLevel() == 0) {
                pauseSnowing = true;
                if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                    snowflakes.clear();
                    lockSnowflakes.unlock();
                }
            } else {
                pauseSnowing = flagsConfiguration.isAttack();
                snowflakesTimeGen = (int) Utils.linearInterpolation(flagsConfiguration.getSnowingLevel(), 1, 100, 10, 7);
                this.resetSnowingTimer();
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
                    if (!snowflake.isFreezed() && snowflake.getLocation().x > screenBounds.width / 2.0d - 150 && snowflake.getLocation().x < screenBounds.width / 2.0d + 150 && this.isColliding(snowflake, imageData)) {
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
        final Set<RGB> colors = new HashSet<>();
        for (int x = -1; x < 2; x++) {
            Point screenLoc = GuiUtils.toScreenCoord((int) snowflake.getLocation().x + x, (int) snowflake.getLocation().y);
            colors.add(GuiUtils.getPixelColor(imageData, screenLoc.x, screenLoc.y));
        }
        return colors.stream().anyMatch(p -> p.equals(GuiUtils.FREEZE_COLOR));
    }

    public void skipInitialAnimation() {
        skipInitialAnimation = true;
    }

    private void resetSnowingTimer() {
        if (taskSnowing != null) {
            taskSnowing.cancel();
        }
        taskSnowing = new TimerTask() {
            @Override
            public void run() {
                if (!pauseSnowing) {
                    if (lockSnowflakes.tryLock()) {
                        generateNewSnowflake();
                        lockSnowflakes.unlock();
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(taskSnowing, 0, snowflakesTimeGen);
    }

}
