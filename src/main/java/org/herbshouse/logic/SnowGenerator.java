package org.herbshouse.logic;


import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.SnowingApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SnowGenerator extends Thread implements SnowListener {

    private volatile List<Snowflake> snowflakes = new CopyOnWriteArrayList<>();

    private final List<Snowflake> toRemove = new ArrayList<>();
    private volatile boolean shutdown = false;

    private final Rectangle drawingSurface;

    private volatile boolean happyWind = false;

    private volatile boolean normalWind = false;

    private final Map<Snowflake, HappyWindSnowData> happyWindSnowData = new HashMap<>();
    private final ReentrantLock lockSnowflakes = new ReentrantLock(false);

    public SnowGenerator(Rectangle drawingSurface) {
        this.drawingSurface = drawingSurface;
    }

    public List<Snowflake> getSnowflakes() {
        return snowflakes;
    }

    @Override
    public void run() {
        if (SnowingApplication.DEBUG_PATH) {
            int size = 3;
            Snowflake snowflake = this.generateNewSnowflake(
                    size,
                    Utils.linearInterpolation(size, 1, 0.3, 10, 2)
            );
        }

        while (!shutdown) {
            if (lockSnowflakes.tryLock()) {
                if (!SnowingApplication.DEBUG_PATH) {
                      this.generateNewSnowflake();
                }

                //Move all snowflakes
                for (Snowflake snowflake : snowflakes) {
                    if (snowflake.isFreezed()){
                        continue;
                    }
                    this.move(snowflake);
                    if (snowflake.getLocation().y > drawingSurface.height) {
                        toRemove.add(snowflake);
                    }
                }
                if (toRemove.size() > 100) {
                    if (happyWind) {
                        toRemove.forEach(happyWindSnowData::remove);
                    }
                    snowflakes.removeAll(toRemove);
                    toRemove.clear();
                }
                lockSnowflakes.unlock();
            }

            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void move(Snowflake snowflake) {
        if (SnowingApplication.DEBUG_PATH) {
            snowflake.registerHistoryLocation();
        }
        Point2D newLoc = snowflake.getLocation().clone();
        if (happyWind) {
            HappyWindSnowData data = happyWindSnowData.get(snowflake);
            newLoc.x = data.getOrigLocation().x + data.getAreaToMove() * Math.sin(data.getAngle());
            data.increaseAngle();
        } else if (normalWind) {
            int startCriticalArea = drawingSurface.height / 4;
            int endCriticalArea = startCriticalArea + 100;
            if (newLoc.y > startCriticalArea && newLoc.y < endCriticalArea) {
                newLoc.x += Utils.linearInterpolation(newLoc.x, 1, 4, drawingSurface.width, 0);
            } else if (newLoc.y > endCriticalArea){
                newLoc.x += Utils.linearInterpolation(newLoc.y, endCriticalArea, 2, drawingSurface.height, 0);
            }
        }
        newLoc.y += snowflake.getSpeed();
        newLoc.x = Math.min(newLoc.x, drawingSurface.width);
        snowflake.setLocation(newLoc);
    }

    private Snowflake generateNewSnowflake() {
        int size = 1 + (int) (Math.random() * 4);
        Snowflake snowflake = generateNewSnowflake(
                size,
                Utils.linearInterpolation(size, 1, 1, 4, 2)
        );
        if (happyWind) {
            initializeSnowFlakeHappyWind(snowflake);
        }
        return snowflake;
    }

    private Snowflake generateNewSnowflake(int size, double speed) {
        final Snowflake snowflake = new Snowflake();
        snowflake.setLocation(new Point2D(Math.random() * drawingSurface.width, -20));
        snowflake.setSize(size);
        snowflake.setSpeed(speed);
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
                happyWind = true;
                lockSnowflakes.unlock();
            } else {
                System.out.println("GUI thread is overloaded !!!");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeSnowFlakeHappyWind(Snowflake snowflake) {
        happyWindSnowData.putIfAbsent(snowflake, new HappyWindSnowData());
        HappyWindSnowData data = happyWindSnowData.get(snowflake);
        data.setOrigLocation(snowflake.getLocation().clone());
        if (SnowingApplication.DEBUG_PATH) {
            data.setAngleIncrease(0.03);
            data.setAreaToMove(50);
        } else {
            data.setAngleIncrease(3 * Math.random() / 100);
            data.setAreaToMove(Math.abs((int) (Math.random() * 400)));
        }
        snowflake.setSpeed(Utils.linearInterpolation(data.getAreaToMove(), 100, 3, 1, 1));
    }

    @Override
    public void turnOffHappyWind() {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                happyWind = false;
                happyWindSnowData.clear();
                lockSnowflakes.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void turnOnNormalWind() {
        normalWind = true;
    }

    @Override
    public void turnOffNormalWind() {
        normalWind = false;
    }

    @Override
    public void freezeSnowflakes(List<Snowflake> snowflakes) {
        try {
            if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
                for (Snowflake snowflake : snowflakes){
                    snowflake.setFreezed();
                }
                lockSnowflakes.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        shutdown = true;
    }


}
