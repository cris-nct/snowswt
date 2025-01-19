package org.herbshouse.gui.imageBuilder;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.herbshouse.controller.LogicController;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.fractals.ITree;
import org.herbshouse.logic.fractals.Tree;
import org.herbshouse.logic.fractals.TreeBranch;

public class FractalsDrawer {

  private Image image;

  private final LogicController controller;

  public FractalsDrawer(LogicController controller) {
    this.controller = controller;
  }

  public void draw(GC gc, GeneratorListener<Tree> generatorListener) {
    if (gc == null) {
      throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
    }
    List<Tree> trees = generatorListener.getMoveableObjects();
    if ((image == null && !trees.isEmpty()) || generatorListener.isForceRedraw()) {
      if (generatorListener.isForceRedraw()) {
        generatorListener.setForceRedraw(false);
      }
      image = new Image(gc.getDevice(), gc.getClipping());
      GC newGC = new GC(image);
      newGC.setBackground(gc.getBackground());
      newGC.setForeground(gc.getForeground());
      newGC.fillRectangle(gc.getClipping());
      newGC.setAntialias(SWT.ON);
      newGC.setLineCap(SWT.CAP_ROUND);
      for (Tree tree : trees) {
        this.draw(newGC, tree);
      }
      newGC.setAntialias(SWT.DEFAULT);
      newGC.dispose();
    }
    if (image != null && controller.getFlagsConfiguration().isFractals()) {
      gc.drawImage(image, 0, 0);
    }
  }

  private void draw(GC gc, ITree tree) {
    GuiUtils.drawLine(gc, tree.getStart(), tree.getEnd(), tree.getThickness());
    for (TreeBranch branch : tree.getBranches()) {
      draw(gc, branch);
    }
  }

  public void dispose() {
    if (image != null && !image.isDisposed()) {
      image.dispose();
    }
  }
}
