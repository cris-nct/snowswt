package org.herbshouse.logic.fractals.visitor;

import java.util.HashMap;
import java.util.Map;
import org.herbshouse.logic.fractals.Tree;

public class TreeBranchesVisitor {

  private final Map<Tree, TreeVisitorThread> visitorThreads = new HashMap<>();

  public void startVisiting(Tree tree) {
    TreeVisitorThread thread = new TreeVisitorThread(tree);
    thread.start();
    visitorThreads.put(tree, thread);
    thread.setCallback(() -> visitorThreads.remove(tree));
  }

  public boolean isVisiting(Tree tree) {
    return visitorThreads.containsKey(tree);
  }

  public void shutdown() {
    for (TreeVisitorThread thread : visitorThreads.values()) {
      thread.interrupt();
    }
  }

}
