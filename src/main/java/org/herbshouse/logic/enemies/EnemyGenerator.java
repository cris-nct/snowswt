package org.herbshouse.logic.enemies;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class EnemyGenerator extends AbstractGenerator<AbstractMovableObject> {

  public static final RGB RED_COLOR = new RGB(160, 0, 0);
  public static final RGB FREE_MOVE_COLOR = new RGB(50, 180, 180);
  private static final int ANGRY_FACE_SIZE = 150;
  private static final RGB REMOVE_BACKGROUND_COLOR = new RGB(255, 255, 255);
  private final List<RedFace> redFaces = new CopyOnWriteArrayList<>();
  private final List<AnimatedGif> fireGifs = new CopyOnWriteArrayList<>();
  private AnimatedGif angryFace;
  private FlagsConfiguration flagsConfiguration;
  private Rectangle screenBounds;
  private boolean shutdown = false;

  @Override
  public void run() {
    Timer timer = new Timer("EnemiesTimer");
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (flagsConfiguration.isEnemies() && !shutdown) {
          generateRedFace();
        }
      }
    }, 5000, 10000);
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (flagsConfiguration.isEnemies() && !shutdown) {
          for (RedFace redFace : redFaces) {
            if (flagsConfiguration.isAttack()) {
              redFace.setState(RedFaceState.FOLLOW_MOUSE);
            }
            if (redFace.getKissingGif() == null) {
              redFace.decreaseLife(1);
            } else {
              switch (redFace.getState()) {
                case FOLLOW_MOUSE -> getController().getUserInfo().decreasePoints(1);
                case FREE -> getController().getUserInfo().increasePoints(1);
              }
            }
          }
        }
      }
    }, 1000, 1000);

    while (!shutdown) {
      if (flagsConfiguration.isEnemies()) {
        this.move();
        this.checkCollisions();
        Utils.sleep(FlagsConfiguration.SLEEP_ENEMY_GENERATOR);
      } else {
        this.cleanup();
        Utils.sleep(FlagsConfiguration.SLEEP_GENERATOR_DOING_NOTHING);
      }
    }

    this.cleanup();
    timer.cancel();
    timer.purge();
  }

  private void move() {
    for (RedFace redFace : redFaces) {
      if (redFace.getLife() == 0) {
        double[] circlePoints
            = Utils.generateCircle(redFace.getLocation(), redFace.getSize() / 2.0d,
            redFace.getSize(), 0);
        getController().substractAreaFromShell(GuiUtils.toScreenCoord(circlePoints));
        redFaces.remove(redFace);
        continue;
      }
      boolean isMouseInTheBall
          = Utils.distance(redFace.getLocation(), flagsConfiguration.getMouseLoc())
          < redFace.getSize() / 2.0d;
      if (isMouseInTheBall) {
        redFace.startKissing();
      } else {
        redFace.stopKissing();
        if (redFace.getState() == RedFaceState.FOLLOW_MOUSE) {
          redFace.setDirection(
              Utils.angleOfPath(redFace.getLocation(), flagsConfiguration.getMouseLoc()));
        }
        redFace.move();
      }
    }

    for (AnimatedGif fire : fireGifs) {
      fire.move();
      if (fire.getLocation().y < 0) {
        fireGifs.remove(fire);
      }
    }
  }

  @Override
  public void turnOnHappyWind() {

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
      gif.setSpeed(2);
      fireGifs.add(gif);
    }
  }

  @Override
  public void mouseScrolled(int count) {

  }

  @Override
  public void reset() {
    cleanup();
    this.generateRedFace();
    this.getController().resetShellSurface();
  }

  private void cleanup() {
    for (RedFace redFace : redFaces) {
      redFace.stopTimer();
    }
    this.redFaces.clear();
    this.fireGifs.clear();
  }

  @Override
  public void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;

    this.generateRedFace();

    this.angryFace = new AnimatedGif("angry1.gif", 0.3, REMOVE_BACKGROUND_COLOR);
    this.angryFace.setLocation(
        new Point2D(screenBounds.width - ANGRY_FACE_SIZE / 2.0d, ANGRY_FACE_SIZE / 2.0d));
    this.angryFace.setSize(ANGRY_FACE_SIZE);
    this.angryFace.setSpeed(1);
  }

  private void generateRedFace() {
    RedFace redFace = new RedFace();
    int size = 50 + (int) (Math.random() * 100);
    redFace.setLocation(new Point2D(screenBounds.width - size / 2.0d - 10, size / 2.0d + 10));
    redFace.setSize(size);
    redFace.setLife((int) Utils.linearInterpolation(size, 50, 30, 150, 120));
    redFace.setSpeed(1);
    redFace.setDirection(Math.toRadians(89 + Math.random() * 91));
    redFace.setState(RedFaceState.FREE);
    redFace.setStateLazy(10000, RedFaceState.FOLLOW_MOUSE);
    redFaces.add(redFace);
  }

  @Override
  public List<AbstractMovableObject> getMoveableObjects() {
    List<AbstractMovableObject> enemies = new ArrayList<>();
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
  public void provideImageData(ImageData imageData) {
  }

  @Override
  public void switchAttack() {

  }

  private void checkCollisions() {
    for (int i = 0; i < redFaces.size(); i++) {
      //Check collision with walls
      RedFace redFace1 = redFaces.get(i);
      this.checkWallCollision(redFace1);
      this.checkFireCollision(redFace1);
      for (int j = i + 1; j < redFaces.size(); j++) {
        RedFace redFace2 = redFaces.get(j);
        if (isColliding(redFace1, redFace2)) {
          this.computeNewDirections(redFace1, redFace2);
        }
      }
    }
  }

  private void computeNewDirections(RedFace redFace1, RedFace redFace2) {
    double m1 = redFace1.getSize(); // mass of ball 1
    double m2 = redFace2.getSize(); // mass of ball 2

    Point2D vel1 = Utils.moveToDirection(new Point2D(0, 0), redFace1.getSpeed(),
        redFace1.getDirection());
    Point2D vel2 = Utils.moveToDirection(new Point2D(0, 0), redFace2.getSpeed(),
        redFace2.getDirection());

    double[] u1 = {vel1.x, vel1.y}; // initial velocity of ball 1 (vx, vy)
    double[] u2 = {vel2.x, vel2.y}; // initial velocity of ball 2 (vx, vy)

    double[] v1 = new double[2]; // final velocity of ball 1 (vx, vy)
    double[] v2 = new double[2]; // final velocity of ball 2 (vx, vy)

    // Calculate the velocity of the center of mass
    double[] Vcm = {(m1 * u1[0] + m2 * u2[0]) / (m1 + m2), (m1 * u1[1] + m2 * u2[1]) / (m1 + m2)};

    // Calculate the velocities relative to the center of mass
    double[] u1_rel = {u1[0] - Vcm[0], u1[1] - Vcm[1]};
    double[] u2_rel = {u2[0] - Vcm[0], u2[1] - Vcm[1]};

    // Reverse the relative velocities (for elastic collision)
    double[] v1_rel = {-u1_rel[0], -u1_rel[1]};
    double[] v2_rel = {-u2_rel[0], -u2_rel[1]};

    // Convert the relative velocities back to the lab frame
    v1[0] = v1_rel[0] + Vcm[0];
    v1[1] = v1_rel[1] + Vcm[1];
    v2[0] = v2_rel[0] + Vcm[0];
    v2[1] = v2_rel[1] + Vcm[1];

    // Calculate the angles (directions) of the final velocities
    double angle1 = Math.atan2(v1[1], v1[0]);
    double angle2 = Math.atan2(v2[1], v2[0]);

    Point2D newLoc1 = Utils.moveToDirection(redFace1.getLocation(), 5, angle1);
    Point2D newLoc2 = Utils.moveToDirection(redFace2.getLocation(), 5, angle2);
    if (Utils.distance(newLoc1, newLoc2) > (redFace1.getSize() + redFace2.getSize()) / 2.0 + 2) {
      if (!redFace1.isPause()) {
        redFace1.setDirection(angle1);
        if (redFace1.getState() == RedFaceState.FOLLOW_MOUSE) {
          redFace1.setState(RedFaceState.FREE);
          redFace1.setStateLazy(10000, RedFaceState.DAMAGED);
          redFace1.setStateLazy(11000, RedFaceState.FOLLOW_MOUSE);
        }
      }
      if (!redFace2.isPause()) {
        redFace2.setDirection(angle2);
        if (redFace2.getState() == RedFaceState.FOLLOW_MOUSE) {
          redFace2.setState(RedFaceState.FREE);
          redFace2.setStateLazy(10000, RedFaceState.DAMAGED);
          redFace2.setStateLazy(11000, RedFaceState.FOLLOW_MOUSE);
        }
      }
    }
  }

  private void checkFireCollision(RedFace redFace) {
    for (AnimatedGif fire : fireGifs) {
      if (this.isColliding(redFace, fire)) {
        if (redFace.getState() == RedFaceState.FREE) {
          redFace.decreaseLife(50);
          if (redFace.getLife() == 0) {
            redFaces.remove(redFace);
          } else {
            redFace.setState(RedFaceState.DAMAGED);
            redFace.setStateLazy(3000, RedFaceState.FREE);
          }
        } else if (redFace.getState() == RedFaceState.FOLLOW_MOUSE) {
          redFace.setState(RedFaceState.DAMAGED);
          redFace.setStateLazy(1000, RedFaceState.FREE);
          redFace.setStateLazy(5000, RedFaceState.FOLLOW_MOUSE);
        }
        fireGifs.remove(fire);
        break;
      }
    }
  }

  private void checkWallCollision(RedFace redFace) {
    Point2D vel = Utils.moveToDirection(new Point2D(0, 0), redFace.getSpeed(),
        redFace.getDirection());
    int radius = redFace.getSize() / 2;
    double x = redFace.getLocation().x;
    double y = redFace.getLocation().y;

    // Check for collision with the left or right bounds
    if (x - radius < 0) {
      x = radius; // Correct position
      vel.x = -vel.x; // Reverse the horizontal direction
    } else if (x + radius > screenBounds.width) {
      x = screenBounds.width - radius; // Correct position
      vel.y = -vel.y; // Reverse the horizontal direction
    }

    // Check for collision with the top or bottom bounds
    if (y - radius < 0) {
      y = radius;// Correct position
      vel.y = -vel.y; // Reverse the vertical direction
    } else if (y + radius > screenBounds.height) {
      y = screenBounds.height - radius; // Correct position
      vel.y = -vel.y; // Reverse the vertical direction
    }

    redFace.setLocation(new Point2D(x, y));
    redFace.setDirection(Math.atan2(vel.y, vel.x));
  }
}
