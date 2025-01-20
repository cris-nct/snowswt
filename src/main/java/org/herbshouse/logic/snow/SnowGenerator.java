package org.herbshouse.logic.snow;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.audio.AudioPlayOrder;
import org.herbshouse.audio.AudioPlayType;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.nonphase.BigWormAttackStrategy;
import org.herbshouse.logic.snow.attack.impl.nonphase.FireworksStrategy;
import org.herbshouse.logic.snow.attack.impl.nonphase.YinYangAttackStrategy;
import org.herbshouse.logic.snow.attack.impl.phase.blackhole.BlackHoleStrategy;
import org.herbshouse.logic.snow.attack.impl.phase.dancing.DancingSnowflakesStrategy;
import org.herbshouse.logic.snow.attack.impl.phase.parasites.ParasitesAttackStrategy;
import org.herbshouse.logic.snow.data.AbstractAttackData;
import org.herbshouse.logic.snow.data.HappyWindSnowFlakeData;

public class SnowGenerator extends AbstractGenerator<Snowflake> {

  private final List<Snowflake> snowflakes = new CopyOnWriteArrayList<>();
  private final ReentrantLock lockSnowflakes = new ReentrantLock(false);
  private final Timer timer;
  private final Map<Integer, AttackStrategy<?>> attackStrategies = new HashMap<>();
  private final InitialAnimation initialAnimation;
  private Rectangle screenBounds;
  private FlagsConfiguration flagsConfiguration;
  private ImageData imageData;
  private TimerTask taskSnowing;
  private boolean shutdown = false;
  private boolean skipInitialAnimation;
  private boolean pauseSnowing;
  private int counterFreezeSnowklakes;
  private int snowflakesTimeGen = 100;

  public SnowGenerator() {
    timer = new Timer("SnowGeneratorTimer");
    initialAnimation = new InitialAnimation(this);
  }

  @Override
  public List<Snowflake> getMoveableObjects() {
    return snowflakes;
  }

  @Override
  public void run() {
    if (!skipInitialAnimation) {
      initialAnimation.run();
    }
    this.resetSnowingTimer();
    while (!shutdown) {
      this.update();
      if (!flagsConfiguration.isAttack() && counterFreezeSnowklakes < 3000
          && !flagsConfiguration.isDebug()) {
        this.checkCollisions();
      }
      Utils.sleep(getSleepDuration());
    }

    removeSnowflakes(snowflakes);
    attackStrategies.values().forEach(AttackStrategy::shutdown);
    taskSnowing.cancel();
    timer.cancel();
    timer.purge();
  }

  void removeSnowflakes(List<Snowflake> snowflakeList) {
    for (Snowflake snowflake : snowflakeList) {
      if (snowflake.getIndividualStrategy() != null) {
        snowflake.getIndividualStrategy().shutdown();
      }
    }
    snowflakes.removeAll(snowflakeList);
  }

  void update() {
    if (lockSnowflakes.tryLock()) {
      //Move all snowflakes
      Snowflake prevSnowFlake = null;
      int snowflakeindex = 0;
      counterFreezeSnowklakes = 0;
      List<Snowflake> toRemove = new ArrayList<>();

      for (Snowflake snowflake : snowflakes) {
        if (snowflake.isFreezed()) {
          counterFreezeSnowklakes++;
          continue;
        }
        boolean attackMode = this.move(snowflake, prevSnowFlake, snowflakeindex);
        if (snowflake.getLocation().y <= 0 && !attackMode) {
          toRemove.add(snowflake);
        } else {
          prevSnowFlake = snowflake;
        }
        snowflakeindex++;
      }

      if (!toRemove.isEmpty()) {
        removeSnowflakes(toRemove);
      }

      if (!snowflakes.isEmpty()) {
        if (flagsConfiguration.isAttack()) {
          AttackStrategy<?> strategy = this.getAttackStrategy();
          if (strategy.isStarted()) {
            strategy.afterUpdate(snowflakes);
            AbstractAttackData data = strategy.getData(snowflakes.getFirst());
            int phase = 0;
            if (data instanceof AbstractPhaseAttackData) {
              phase = ((AbstractPhaseAttackData) data).getPhase();
            }
            getLogicController().setCurrentAttackPhase(phase);
          }
        }
        for (Snowflake snowflake : snowflakes) {
          if (snowflake.getIndividualStrategy() != null && snowflake.getIndividualStrategy().isStarted()) {
            snowflake.getIndividualStrategy().afterUpdate(List.of(snowflake));
          }
        }
      }

      if (snowflakes.isEmpty() && flagsConfiguration.isDebug()) {
        generateNewSnowflake(20, 1);
      }
      lockSnowflakes.unlock();
    }
  }

  private AttackStrategy<?> getAttackStrategy() {
    return attackStrategies.get(flagsConfiguration.getAttackType());
  }

  @Override
  public int getCountdown() {
    return initialAnimation.getCountdown();
  }

  boolean move(Snowflake snowflake, Snowflake prevSnowFlake, int index) {
    snowflake.getSnowTail().registerHistoryLocation();
    AttackStrategy<?> attackStrategy = getAttackStrategy();
    boolean attackMode = flagsConfiguration.isAttack() && attackStrategy.isStarted()
        && index < attackStrategy.getMaxSnowflakesInvolved();

    if (snowflake.getIndividualStrategy() != null) {
      snowflake.setLocation(
          snowflake.getIndividualStrategy().computeNextLocation(snowflake, prevSnowFlake));
    } else if (attackMode) {
      snowflake.setLocation(attackStrategy.computeNextLocation(snowflake, prevSnowFlake));
    } else if (flagsConfiguration.isHappyWind()) {
      this.moveSnowflakeHappyWind(snowflake);
    } else if (flagsConfiguration.isNormalWind()) {
      this.moveSnowflakeNormalWind(snowflake);
    } else {
      this.regularSnowing(snowflake);
    }
    return attackMode;
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
    HappyWindSnowFlakeData data = getHappyWindData(snowflake);
    if (data.isMoveSinusoidal()) {
      newLoc.x = data.getOrigLocation().x + data.getAreaToMove() * Math.sin(data.getAngle());
    } else {
      newLoc.x += data.getStepX();
    }
    newLoc.y -= snowflake.getSpeed();
    snowflake.setLocation(newLoc);
  }

  private HappyWindSnowFlakeData getHappyWindData(Snowflake snowflake) {
    HappyWindSnowFlakeData data = (HappyWindSnowFlakeData) snowflake.getData("HAPPYWIND");
    if (data == null) {
      data = new HappyWindSnowFlakeData();
      snowflake.setData("HAPPYWIND", data);
      this.initializeSnowFlakeHappyWind(snowflake);
    }
    return data;
  }

  @SuppressWarnings("UnusedReturnValue")
  public Snowflake generateNewSnowflake() {
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
    HappyWindSnowFlakeData data = getHappyWindData(snowflake);
    data.setOrigLocation(snowflake.getLocation().clone());
    final double speed;
    if (Math.random() > 0.6) {
      double part = Math.random();
      double stepX;
      if (part >= 0.33 && part <= 0.66) {
        stepX = 0;
        speed = 0.5 + Math.random();
      } else {
        stepX = 0.2 + 0.3 * Math.random();
        if (part < 0.33) {
          stepX = -stepX;
        }
        speed = Utils.linearInterpolation(Math.abs(stepX), 0, 0.5 + Math.random(), 1,
            1 + Math.random());
      }
      data.setStepX(stepX);
      data.setMoveSinusoidal(false);
    } else {
      speed = 0.5 + Math.random();
      data.setMoveSinusoidal(true);
      data.setAngleIncrease(Math.toRadians(0.2 * Math.random()));
      data.setAreaToMove(50 + (int) (Math.random() * 50));
    }
    snowflake.setSpeed(speed);
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
          removeSnowflakes(snowflakes);
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
      if (initialAnimation.getCountdown() == -1) {
        if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
          removeSnowflakes(snowflakes);
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
    this.registerAttackLogic(new BigWormAttackStrategy(flagsConfiguration));
    this.registerAttackLogic(new DancingSnowflakesStrategy(flagsConfiguration, screenBounds));
    this.registerAttackLogic(new ParasitesAttackStrategy(flagsConfiguration, screenBounds));
    this.registerAttackLogic(new YinYangAttackStrategy(screenBounds));
    this.registerAttackLogic(new BlackHoleStrategy(flagsConfiguration, screenBounds, getLogicController().getAudioPlayer()));
  }

  public Rectangle getScreenBounds() {
    return screenBounds;
  }

  private void registerAttackLogic(AttackStrategy<?> strategy) {
    if (this.attackStrategies.get(strategy.getAttackType()) != null) {
      throw new IllegalArgumentException("Ovveride strategy for attack!!");
    }
    this.attackStrategies.put(strategy.getAttackType(), strategy);
  }

  @Override
  public void mouseDown(int button, Point2D mouseLocation) {
    if (button == 1) {
      try {
        if (lockSnowflakes.tryLock(1, TimeUnit.SECONDS)) {
          for (int i = 0; i < 5; i++) {
            Snowflake snowflake = generateNewSnowflake();
            snowflake.setLocation(mouseLocation);
            snowflake.setSize(7);
            snowflake.setColor(new RGB(255, 140, 0));
            AttackStrategy<?> strategy = new FireworksStrategy(flagsConfiguration, screenBounds);
            strategy.beforeStart(List.of(snowflake));
            snowflake.setIndividualStrategy(strategy);
          }
          lockSnowflakes.unlock();
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    } else if (button == 3) {
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
    try {
      if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
        if (flagsConfiguration.isAttack()) {
          pauseSnowing = true;
          //Removed all freezed snoflakes
          removeSnowflakes(snowflakes.stream().filter(Snowflake::isFreezed).toList());
          this.cleanupSnowflakesData();
          this.getAttackStrategy().beforeStart(snowflakes);
        } else {
          pauseSnowing = false;
          this.cleanupSnowflakesData();
        }
        lockSnowflakes.unlock();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void changeAttackType() {
    try {
      if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
        this.cleanupSnowflakesData();
        this.getAttackStrategy().beforeStart(snowflakes);
      }
      lockSnowflakes.unlock();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void switchBlackHoles() {
    if (flagsConfiguration.isBlackHoles()) {
      AudioPlayOrder order = new AudioPlayOrder("blackhole.wav");
      order.setType(AudioPlayType.BACKGROUND);
      order.setVolume(0.9f);
      getLogicController().getAudioPlayer().play(order);
    } else {
      getLogicController().getAudioPlayer().stop("blackhole.wav");
      try {
        if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
          for (Snowflake snowflake : snowflakes) {
            if (snowflake.getIndividualStrategy() != null) {
              snowflake.getIndividualStrategy().shutdown();
              snowflake.setIndividualStrategy(null);
              snowflake.setShowTrail(false);
              snowflake.setAlpha(255);
              snowflake.setSize(5);
            }
          }
        }
        lockSnowflakes.unlock();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void cleanupSnowflakesData() {
    for (Snowflake snowflake : snowflakes) {
      snowflake.cleanup();
    }
  }

  @Override
  public void changedSnowingLevel() {
    try {
      if (initialAnimation.getCountdown() == -1 && flagsConfiguration.getSnowingLevel() == 0) {
        pauseSnowing = true;
        if (lockSnowflakes.tryLock(10, TimeUnit.SECONDS)) {
          removeSnowflakes(snowflakes);
          lockSnowflakes.unlock();
        }
      } else {
        pauseSnowing = flagsConfiguration.isAttack();
        snowflakesTimeGen = (int) Utils.linearInterpolation(flagsConfiguration.getSnowingLevel(), 1,
            100, 10, 7);
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
      if (imageData == null) {
        return;
      }
      if (lockSnowflakes.tryLock(2, TimeUnit.SECONDS)) {
        List<Snowflake> toRemove = new ArrayList<>();
        for (Snowflake snowflake : snowflakes) {
          if (snowflake.getIndividualStrategy() == null) {
            if (!snowflake.isFreezed()
                && snowflake.getLocation().x > screenBounds.width / 2.0d - 150
                && snowflake.getLocation().x < screenBounds.width / 2.0d + 150
                && this.isColliding(snowflake, imageData)) {
              snowflake.freeze();
            }
            if (flagsConfiguration.isBlackHoles()
                && Utils.distance(flagsConfiguration.getMouseLoc(), snowflake.getLocation()) < BlackHoleStrategy.BLACKHOLE_RADIUS) {
              toRemove.add(snowflake);
            }
          }
        }
        removeSnowflakes(toRemove);
        lockSnowflakes.unlock();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isColliding(Snowflake snowflake, ImageData imageData) {
    final Set<RGB> colors = new HashSet<>();
    for (int x = -1; x < 2; x++) {
      Point screenLoc = GuiUtils.toScreenCoord((int) snowflake.getLocation().x + x,
          (int) snowflake.getLocation().y);
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
            Snowflake snowflake = generateNewSnowflake();
            if (flagsConfiguration.isBlackHoles()) {
              long snowflakesBlackHole = snowflakes.stream().filter(s -> s.getIndividualStrategy() instanceof BlackHoleStrategy).count();
              if (snowflakesBlackHole < BlackHoleStrategy.BLACKHOLES_MAX_SNOWFLAKES) {
                boolean stillOneSnowflakeToFinish = snowflakes.stream()
                    .anyMatch(s -> s.getIndividualStrategy() instanceof BlackHoleStrategy
                        && ((BlackHoleStrategy) s.getIndividualStrategy()).getCurrentPhaseProcessor().getCurrentPhaseIndex() == 6
                        && !((BlackHoleStrategy) s.getIndividualStrategy()).getCurrentPhaseProcessor().isFinished(s)
                    );
                if (!stillOneSnowflakeToFinish) {
                  AttackStrategy<?> strategy = new BlackHoleStrategy(flagsConfiguration, screenBounds, getLogicController().getAudioPlayer());
                  strategy.beforeStart(List.of(snowflake));
                  snowflake.setIndividualStrategy(strategy);
                }
              }
            }
            lockSnowflakes.unlock();
          }
        }
      }
    };
    timer.scheduleAtFixedRate(taskSnowing, 0, snowflakesTimeGen);
  }

  @Override
  protected int getSleepDuration() {
    return 5;
  }

  public List<Snowflake> getSnowflakes() {
    return snowflakes;
  }

  void addSnowflake(Snowflake snowflake) {
    this.snowflakes.add(snowflake);
  }

}
