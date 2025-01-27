package org.herbshouse.logic.fractals.visitor;

import org.herbshouse.logic.Utils;
import org.herbshouse.logic.fractals.ITree;
import org.herbshouse.logic.fractals.Tree;
import org.herbshouse.logic.fractals.TreeBranch;

public class TreeVisitorThread extends Thread {

  private final Tree tree;

  private Runnable callback;

  public TreeVisitorThread(Tree tree) {
    this.tree = tree;
    this.setName(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Override
  public void run() {
    TreeVisitorData data = (TreeVisitorData) tree.getData("TreeFillData");
    if (data == null) {
      data = new TreeVisitorData();
      tree.setData("TreeFillData", data);
    }
    this.update(tree, data);
    Utils.sleep(2000);
    data.cleanup();
    if (callback != null) {
      callback.run();
    }
  }

  private void update(ITree branch, TreeVisitorData data) {
    data.setVisitedBranch(branch);
    for (TreeBranch subBranch : branch.getBranches()) {
      data.setVisitedBranch(subBranch);
    }
    Utils.sleep(0, 5);
    for (int i = branch.getBranches().size() - 1; i >= 0; i--) {
      this.update(branch.getBranches().get(i), data);
    }
  }

  public void setCallback(Runnable callback) {
    this.callback = callback;
  }

}
