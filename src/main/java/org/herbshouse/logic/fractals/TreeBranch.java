package org.herbshouse.logic.fractals;

import java.util.ArrayList;
import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class TreeBranch implements ITree {

  private Point2D start;

  private Point2D end;

  private double thickness = 1;

  private final List<TreeBranch> subBranches = new ArrayList<>();

  public TreeBranch() {

  }

  public TreeBranch(Point2D start, Point2D end) {
    this.start = start;
    this.end = end;
  }

  public void setThickness(double thickness) {
    this.thickness = thickness;
  }

  @Override
  public double getLength() {
    return Utils.distance(start, end);
  }

  @Override
  public double getThickness() {
    return thickness;
  }

  public void setEnd(Point2D end) {
    this.end = end;
  }

  public void setStart(Point2D start) {
    this.start = start;
  }

  @Override
  public Point2D getEnd() {
    return end;
  }

  @Override
  public Point2D getStart() {
    return start;
  }

  @Override
  public void addBranch(TreeBranch branch) {
    subBranches.add(branch);
  }

  @Override
  public List<TreeBranch> getBranches() {
    return subBranches;
  }


}
