package org.herbshouse.gui;

import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import java.util.concurrent.atomic.AtomicInteger;

public class RenderingEngine implements Runnable, IDrawCompleteListener {

    private final Canvas canvas;

    private final AtomicInteger counterFrames = new AtomicInteger(0);

    private long startTimeCounter;

    private int realFPS;

    public RenderingEngine(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() - startTimeCounter >= 1000) {
            realFPS = counterFrames.get();
            startTimeCounter = System.currentTimeMillis();
            counterFrames.set(0);
        }
        //Equivalent with canvas.redraw() and canvas.update() but more efficient
        OS.RedrawWindow(canvas.handle, null, 0, OS.RDW_INVALIDATE | OS.RDW_UPDATENOW);
        if (!canvas.isDisposed()) {
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
