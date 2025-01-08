package org.herbshouse.gui;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.controller.FlagsConfiguration;

public class RenderingEngine implements Runnable, IDrawCompleteListener {

  private final Canvas canvas;

  private final AtomicInteger counterFrames = new AtomicInteger(0);
  private final Timer timer;
  private int realFPS;

  public RenderingEngine(Canvas canvas) {
    this.canvas = canvas;
    this.timer = new Timer("RederingEngineTimer");
    this.timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        realFPS = counterFrames.get();
        counterFrames.set(0);
      }
    }, 1000, 1000);
    this.canvas.addDisposeListener(e -> {
      timer.cancel();
      timer.purge();
    });
  }

  @Override
  public void run() {
    if (!canvas.isDisposed()) {
      //Equivalent with canvas.redraw() and canvas.update() but more efficient
      OS.RedrawWindow(canvas.handle, null, 0, OS.RDW_INVALIDATE | OS.RDW_UPDATENOW);
      Display.getDefault().timerExec(1000 / FlagsConfiguration.DESIRED_FPS, this);
    }
  }

  public int getRealFPS() {
    return realFPS;
  }

  @Override
  public void drawCompleted() {
    counterFrames.incrementAndGet();
  }

}
