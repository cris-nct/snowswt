package org.herbshouse.logic.blackhole;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;

public class BlackholeGenerator extends AbstractGenerator<Snowflake> {

  private final List<Snowflake> snowflakes = new CopyOnWriteArrayList<>();
  private final ReentrantLock lockSnowflakes = new ReentrantLock(false);
  private final Timer timer;

  private boolean shutdown = false;
  private BlackHoleStrategy strategy;
  private int slowCounter;

  public BlackholeGenerator() {
    timer = new Timer("SnowGeneratorBlackholeTimer");
  }

  @Override
  public void run() {
    while (!shutdown) {
      if (!getFlagsConfiguration().isPause() && getFlagsConfiguration().isBlackHoles()) {
        this.update();
        int slowCounterStep = (int) Utils.linearInterpolation(getFlagsConfiguration().getSnowingLevel(),
            1, 1, 10, 10
        );
        slowCounterStep = Math.max(slowCounterStep, 1);
        slowCounter++;
        if (slowCounter % slowCounterStep == 0) {
          Utils.sleep(0, 1000);
          slowCounter = 0;
        }
      } else {
        Utils.sleep(getSleepDurationDoingNothing());
      }
    }

    removeSnowflakes(snowflakes);
    strategy.shutdown();
    timer.cancel();
    timer.purge();
  }

  public boolean shouldSwallowPoint(Point2D location) {
    return Utils.distance(getFlagsConfiguration().getMouseLoc(), location) < BlackHoleStrategy.BLACKHOLE_RADIUS;
  }

  private void stopBackgroundSounds() {
    getLogicController().getAudioPlayer().stop("sounds/blackhole.wav");
    getLogicController().getAudioPlayer().stop("sounds/blackhole-2.wav");
    getLogicController().getAudioPlayer().stop("sounds/blackhole-3.wav");
    getLogicController().getAudioPlayer().stop("sounds/explosion.wav");
  }

  @Override
  public void init(FlagsConfiguration flagsConfiguration, Rectangle drawingSurface) {
    super.init(flagsConfiguration, drawingSurface);
    this.strategy = new BlackHoleStrategy(flagsConfiguration, screenBounds, getLogicController().getAudioPlayer());
  }

  void move(Snowflake snowflake, Snowflake prevSnowFlake) {
    snowflake.getSnowTail().registerHistoryLocation();
    final Point2D nextLocation = strategy.computeNextLocation(snowflake, prevSnowFlake);
    snowflake.setLocation(nextLocation);
  }

  void removeSnowflakes(List<Snowflake> snowflakeList) {
    snowflakes.removeAll(snowflakeList);
  }

  void update() {
    if (lockSnowflakes.tryLock()) {
      //Move all snowflakes
      Snowflake prevSnowFlake = null;

      for (Snowflake snowflake : snowflakes) {
        if (snowflake.isFreezed()) {
          continue;
        }
        this.move(snowflake, prevSnowFlake);
        prevSnowFlake = snowflake;
      }

      if (!snowflakes.isEmpty()) {
        if (strategy.isStarted()) {
          strategy.afterUpdate(snowflakes);
          AbstractPhaseAttackData data = strategy.getData(snowflakes.getFirst());
          getLogicController().setCurrentAttackPhase(data.getPhase());
        }
        strategy.afterUpdate(snowflakes);
      }
      lockSnowflakes.unlock();
    }
  }

  private void generateNewSnowflake() {
    final Snowflake snowflake = new Snowflake();
    snowflake.setLocation(new Point2D(-1, -1));
    snowflake.setColor(new RGB(255, 255, 0));
    snowflake.setSpeed(0.3 + Math.random() * 0.7);
    final int size;
    if (config.isBigBalls()) {
      size = 12 + (int) (Math.random() * 20);
    } else {
      size = 2 + (int) (Math.random() * 6);
    }
    snowflake.setSize(size);
    snowflakes.add(snowflake);
  }

  @Override
  protected int getSleepDuration() {
    return 0;
  }

  @Override
  public void turnOnHappyWind() {

  }

  @Override
  public void switchDebug() {

  }

  @Override
  public void mouseMove(Point2D mouseLocation) {

  }

  @Override
  public void mouseDown(int button, Point2D mouseLocation) {

  }

  @Override
  public void mouseScrolled(int count) {

  }

  @Override
  public void reset() {
    try {
      if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
        removeSnowflakes(snowflakes);
        lockSnowflakes.unlock();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Snowflake> getMoveableObjects() {
    return snowflakes;
  }

  @Override
  public void shutdown() {
    shutdown = true;
  }

  @Override
  public void provideImageData(ImageData imageData) {

  }

  @Override
  public void switchAttack() {

  }

  @Override
  public void changeAttackType(int oldAttackType, int newAttackType) {

  }

  @Override
  public void switchBlackHoles() {
    if (getFlagsConfiguration().isBlackHoles()) {
      strategy.beforeStart(snowflakes);
      for (int i = 0; i < strategy.getMaxSnowflakesInvolved(); i++) {
        generateNewSnowflake();
      }
    } else {
      try {
        if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
          stopBackgroundSounds();
          snowflakes.clear();
          lockSnowflakes.unlock();
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      removeSnowflakes(snowflakes);
    }
  }

  @Override
  public void switchIndividualMovements() {

  }

  @Override
  public boolean canControllerStart() {
    return true;
  }

  @Override
  public void switchGraphicalSounds() {

  }
}
