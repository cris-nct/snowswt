package org.herbshouse.gui;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class RenderingEngine implements Runnable, IDrawCompleteListener {

    public static final int FPS = 25;

    private final Canvas canvas;

    private int counterFrames;

    private long startTimeCounter;

    private final SnowShell shell;

    private int realFPS;

    public RenderingEngine(SnowShell shell, Canvas canvas) {
        this.shell = shell;
        this.canvas = canvas;
        this.shell.registerListener(this);
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() - startTimeCounter >= 1000) {
            realFPS = counterFrames;
            startTimeCounter = System.currentTimeMillis();
            counterFrames = 0;
        }
        canvas.redraw();
        canvas.update();
        if (!canvas.isDisposed()) {
            Display.getDefault().timerExec(1000 / FPS, this);
        }
    }

    public int getRealFPS() {
        return realFPS;
    }

    @Override
    public void complete() {
        counterFrames++;
    }

}
