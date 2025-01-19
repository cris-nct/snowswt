package org.herbshouse.logic.fractals;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.AbstractGenerator;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class FractalsGenerator extends AbstractGenerator<Tree> {

  public static final double IMPERFECTTION_FACTOR = 0.07;
  private final Map<TreeType, Tree> trees = new HashMap<>();

  private FlagsConfiguration config;
  private Rectangle screenBounds;
  private boolean shutdown = false;
  private boolean forceRedraw = false;
  private double counterWind = 0;
  private int counterWindDir = 1;

  @Override
  public void run() {
    for (TreeType val : TreeType.values()) {
      trees.put(val, generateTree(val));
    }
    while (!shutdown) {
      if (config.isFractals()) {
        boolean animate = config.isNormalWind() && (config.getFractalsType() == TreeType.PERFECT_DEFAULT
            || config.getFractalsType() == TreeType.PERFECT_FIR);
        if (animate) {
          trees.put(config.getFractalsType(), generateTree(config.getFractalsType()));
          forceRedraw = true;
          Utils.sleep(getSleepDuration());
        } else {
          Utils.sleep(getSleepDurationDoingNothing());
        }
      } else {
        Utils.sleep(getSleepDurationDoingNothing());
      }
    }
  }

  @Override
  protected int getSleepDurationDoingNothing() {
    return 5000;
  }

  @Override
  public boolean isForceRedraw() {
    return forceRedraw;
  }

  @Override
  public void setForceRedraw(boolean forceRedraw) {
    this.forceRedraw = forceRedraw;
  }

  @Override
  protected int getSleepDuration() {
    return 500;
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
    this.config = flagsConfiguration;
    this.screenBounds = screenBounds;
  }

  private Tree generateTree(TreeType type) {
    Tree tree = new Tree();
    tree.setLocation(new Point2D(screenBounds.width * 0.3, -250));
    tree.setSize(260);
    tree.setThickness(20);
    tree.setAngle(Math.PI / 2);

    if (counterWind > 1 || counterWind < 0) {
      counterWindDir *= -1;
    }
    counterWind += 0.1 * counterWindDir;

    switch (type) {
      case PERFECT_DEFAULT:
        this.generateBranchesDefault(tree, true);
        break;
      case PERFECT_FIR:
        this.generateBranchesFir(tree, true);
        break;
      case RANDOM_DEFAULT:
        this.generateBranchesDefault(tree, false);
        break;
      case RANDOM_FIR:
        this.generateBranchesFir(tree, false);
        break;
    }
    return tree;
  }

  @Override
  public void changedFractalType() {
    trees.put(config.getFractalsType(), generateTree(config.getFractalsType()));
    forceRedraw = true;
    counterWind = 0;
  }

  private void generateBranchesFir(ITree parent, boolean perfect) {
    double parentLength = parent.getLength();
    double mainBranchLength = parentLength * 0.8;
    if (mainBranchLength <= 1) {
      return;
    }

    double angleMainBranch = parent.getAngle();
    double angleLeftBranch = Utils.normAngle(angleMainBranch + Math.toRadians(110));
    double angleRightBranch = Utils.normAngle(angleMainBranch - Math.toRadians(110));
    if (!perfect) {
      angleMainBranch *= 1 + angleMainBranchIncrease();
    }
    if (config.isNormalWind()) {
      angleMainBranch -= counterWind;
    }

    TreeBranch mainBranch = new TreeBranch();
    mainBranch.setStart(parent.getEnd());
    mainBranch.setEnd(Utils.moveToDirection(mainBranch.getStart(), mainBranchLength, angleMainBranch));
    mainBranch.setThickness(Math.max(parent.getThickness() * 0.7, 1));
    parent.addBranch(mainBranch);

    parent.addBranch(this.generateBranch(mainBranch, 0.8, angleLeftBranch, perfect));
    parent.addBranch(this.generateBranch(mainBranch, 0.8, angleRightBranch, perfect));

    this.generateBranchesFir(mainBranch, perfect);
  }

  private double angleMainBranchIncrease() {
    return IMPERFECTTION_FACTOR * (2 * Math.random() - 1);
  }

  private TreeBranch generateBranch(TreeBranch mainBranch, double locOnMainBranch, double angle, boolean perfect) {
    TreeBranch branch = new TreeBranch();
    double mainBranchLength = mainBranch.getLength();
    branch.setStart(Utils.moveToDirection(mainBranch.getStart(), mainBranchLength * locOnMainBranch, mainBranch.getAngle()));
    branch.setEnd(Utils.moveToDirection(branch.getStart(), mainBranchLength * 0.4, angle));
    branch.setThickness(Math.max(mainBranch.getThickness() * 0.7, 1));
    this.generateBranchesFir(branch, perfect);
    return branch;
  }

  private void generateBranchesDefault(ITree parent, boolean perfect) {
    double parentLength = parent.getLength();
    double mainBranchLength = parentLength * 0.8;
    if (mainBranchLength <= 1) {
      return;
    }

    double angleMainBranch = parent.getAngle();
    double angleLeftBranch = angleMainBranch + Math.PI / 4;
    double angleRightBranch = angleMainBranch - Math.PI / 4;
    if (!perfect) {
      angleMainBranch *= 1 + angleMainBranchIncrease();
    }

    if (config.isNormalWind()) {
      angleMainBranch -= counterWind;
    }

    TreeBranch mainBranch = new TreeBranch();
    mainBranch.setStart(parent.getEnd());
    mainBranch.setEnd(Utils.moveToDirection(mainBranch.getStart(), mainBranchLength, angleMainBranch));
    mainBranch.setThickness(Math.max(parent.getThickness() * 0.7, 1));
    parent.addBranch(mainBranch);

    TreeBranch leftBranch = new TreeBranch();
    leftBranch.setStart(Utils.moveToDirection(mainBranch.getStart(), mainBranchLength * 0.7, angleMainBranch));
    leftBranch.setEnd(Utils.moveToDirection(leftBranch.getStart(), mainBranchLength * 0.4, angleLeftBranch));
    leftBranch.setThickness(Math.max(parent.getThickness() * 0.3, 1));
    parent.addBranch(leftBranch);

    TreeBranch rightBranch = new TreeBranch();
    rightBranch.setStart(Utils.moveToDirection(mainBranch.getStart(), mainBranchLength * 0.3, angleMainBranch));
    rightBranch.setEnd(Utils.moveToDirection(rightBranch.getStart(), mainBranchLength * 0.4, angleRightBranch));
    rightBranch.setThickness(Math.max(parent.getThickness() * 0.3, 1));
    parent.addBranch(rightBranch);

    this.generateBranchesDefault(mainBranch, perfect);
    this.generateBranchesDefault(leftBranch, perfect);
    this.generateBranchesDefault(rightBranch, perfect);
  }

  @Override
  public List<Tree> getMoveableObjects() {
    if (config.isFractals()) {
      return Collections.singletonList(trees.get(config.getFractalsType()));
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
