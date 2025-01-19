package org.herbshouse.logic.fractals;

import java.util.List;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;

public interface ITree {

  double getLength();

  double getThickness();

  void addBranch(TreeBranch branch);

  List<TreeBranch> getBranches();

  Point2D getStart();

  Point2D getEnd();

  default double getAngle() {
    return Utils.angleOfLine(getStart(), getEnd());
  }
}
