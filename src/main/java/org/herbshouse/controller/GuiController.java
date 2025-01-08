package org.herbshouse.controller;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.GuiListener;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.Point2D;

public class GuiController extends AbstractController {

  private final GuiListener listener;

  private final Transform transform;

  private int desiredFPS;

  public GuiController(GuiListener listener) {
    this.listener = listener;
    this.transform = listener.getTransform();
  }

  public void setDesiredFPS(int desiredFPS) {
    this.desiredFPS = desiredFPS;
  }

  @Override
  public int getDesiredFps() {
    return desiredFPS;
  }

  @Override
  public void mouseMove(int x, int y) {
    Point2D mouseLoc = GuiUtils.toWorldCoord(convertLoc(x, y));
    getFlagsConfiguration().setMouseCurrentLocation(mouseLoc);
    getListeners().forEach(l -> l.mouseMove(mouseLoc));
  }

  @Override
  public void mouseDown(int button, int x, int y) {
    Point2D mouseLoc = GuiUtils.toWorldCoord(convertLoc(x, y));
    getListeners().forEach(l -> l.mouseDown(button, mouseLoc));
  }

  private Point convertLoc(int x, int y) {
    int locX = x;
    int locY = y;
    if (getFlagsConfiguration().isFlipImage()) {
      float[] data = {locX, locY};
      transform.transform(data);
      locX = (int) data[0];
      locY = (int) data[1];
    }
    return new Point(locX, locY);
  }

  @Override
  public void setAttackType(int type) {
    super.setAttackType(type);
    if (type == 3 && !getFlagsConfiguration().isObjectsTail()) {
      getFlagsConfiguration().switchObjectsTail();
    }
  }

  @Override
  public void substractAreaFromShell(int[] polygon) {
    Display.getDefault().asyncExec(() -> listener.substractAreaFromShell(polygon));
  }

  @Override
  public void resetShellSurface() {
    Display.getDefault().asyncExec(listener::resetShellSurface);
  }

}
