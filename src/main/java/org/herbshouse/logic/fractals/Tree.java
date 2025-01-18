package org.herbshouse.logic.fractals;

import java.util.ArrayList;
import java.util.List;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class Tree extends AbstractMovableObject implements ITree {

  private double thickness = 1;

  private double angle;

  private final List<TreeBranch> branches = new ArrayList<>();

  public void setAngle(double angle) {
    this.angle = angle;
  }

  @Override
  public double getAngle() {
    return angle;
  }

  @Override
  public void addBranch(TreeBranch branch) {
    branches.add(branch);
  }

  @Override
  public List<TreeBranch> getBranches() {
    return branches;
  }

  @Override
  public Point2D getStart() {
    return getLocation();
  }

  @Override
  public Point2D getEnd() {
    return Utils.moveToDirection(getStart(), getSize(), Math.toRadians(getAngle()));
  }

  public void setThickness(double thickness) {
    this.thickness = thickness;
  }

  @Override
  public double getLength() {
    return getSize();
  }

  @Override
  public double getThickness() {
    return thickness;
  }
}
