package org.herbshouse.logic.fractals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public class Tree extends AbstractMovableObject implements ITree {

  private final List<TreeBranch> branches = new ArrayList<>();
  private final Map<String, TreeData> data = new HashMap<>();
  private double thickness = 1;
  private double angle;

  @Override
  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
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
    return Utils.moveToDirection(getStart(), getSize(), getAngle());
  }

  @Override
  public double getLength() {
    return getSize();
  }

  @Override
  public double getThickness() {
    return thickness;
  }

  public void setThickness(double thickness) {
    this.thickness = thickness;
  }

  public TreeData getData(String key) {
    return data.get(key);
  }

  public void setData(String key, TreeData data) {
    this.data.put(key, data);
  }

  public void cleanup() {
    data.clear();
  }

}
