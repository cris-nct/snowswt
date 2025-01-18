package org.herbshouse.logic.fractals;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class FractalsGenerator extends AbstractGenerator<Tree> {

  private FlagsConfiguration flagsConfiguration;
  private Rectangle screenBounds;
  private boolean shutdown = false;

  private final List<Tree> trees = new CopyOnWriteArrayList<>();

  @Override
  public void run() {
    while (!shutdown) {

      Utils.sleep(getSleepDuration());
    }

  }

  @Override
  protected int getSleepDuration() {
    return 300;
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

  }

  @Override
  public void mouseDown(int button, Point2D mouseLocation) {

  }

  @Override
  public void mouseScrolled(int count) {

  }

  @Override
  public void reset() {

  }

  @Override
  public void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;
    this.generateTree();
  }

  private void generateTree() {
    Tree tree = new Tree();
    tree.setLocation(new Point2D(screenBounds.width * 0.3, -250));
    tree.setSize(260);
    tree.setThickness(20);
    tree.setAngle(90);
    this.generateBranches(tree, false);
    trees.add(tree);
  }

  private void generateBranches(ITree parent, boolean perfect) {
    double parentLength = parent.getLength();
    double mainBranchLength = parentLength * 0.8;
    if (mainBranchLength <= 1) {
      return;
    }

    double angleMainBranch = parent.getAngle();
    double angleLeftBranch = angleMainBranch + 45;
    double angleRightBranch = angleMainBranch - 45;
    if (!perfect) {
      //angleMainBranch *= 1 + (Math.random() * 0.4 - 0.2);
      //angleLeftBranch = angleMainBranch + Utils.linearInterpolation(Math.random(), 0, 30, 1, 60);
      //angleRightBranch = angleMainBranch - Utils.linearInterpolation(Math.random(), 0, 30, 1, 60);
    }

    TreeBranch mainBranch = new TreeBranch();
    mainBranch.setStart(parent.getEnd());
    mainBranch.setEnd(
        Utils.moveToDirection(mainBranch.getStart(), mainBranchLength,
            Math.toRadians(angleMainBranch)));
    mainBranch.setThickness(Math.max(parent.getThickness() * 0.7, 1));
    parent.addBranch(mainBranch);

    TreeBranch leftBranch = new TreeBranch();
    leftBranch.setStart(
        Utils.moveToDirection(mainBranch.getStart(), mainBranchLength * 0.7,
            Math.toRadians(angleMainBranch)));
    leftBranch.setEnd(Utils.moveToDirection(leftBranch.getStart(), mainBranchLength * 0.4,
        Math.toRadians(angleLeftBranch)));
    leftBranch.setThickness(Math.max(parent.getThickness() * 0.3, 1));
    parent.addBranch(leftBranch);

    TreeBranch rightBranch = new TreeBranch();
    rightBranch.setStart(
        Utils.moveToDirection(mainBranch.getStart(), mainBranchLength * 0.3,
            Math.toRadians(angleMainBranch)));
    rightBranch.setEnd(Utils.moveToDirection(rightBranch.getStart(), mainBranchLength * 0.4,
        Math.toRadians(angleRightBranch)));
    rightBranch.setThickness(Math.max(parent.getThickness() * 0.3, 1));
    parent.addBranch(rightBranch);

    this.generateBranches(mainBranch, perfect);
    this.generateBranches(leftBranch, perfect);
    this.generateBranches(rightBranch, perfect);
  }

  @Override
  public List<Tree> getMoveableObjects() {
    if (flagsConfiguration.isFractals()) {
      return trees;
    } else {
      return Collections.emptyList();
    }
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
  public void changeAttackType() {

  }

}
