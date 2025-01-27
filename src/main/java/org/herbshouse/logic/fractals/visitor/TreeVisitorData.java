package org.herbshouse.logic.fractals.visitor;

import java.util.HashSet;
import java.util.Set;
import org.herbshouse.logic.fractals.ITree;
import org.herbshouse.logic.fractals.TreeData;

public class TreeVisitorData implements TreeData {

  private final Set<ITree> visitedBranches = new HashSet<>();

  public void setVisitedBranch(ITree branch) {
    visitedBranches.add(branch);
  }

  public boolean isVisitedBranch(ITree branch) {
    return visitedBranches.contains(branch);
  }

  public void cleanup() {
    visitedBranches.clear();
  }
}
