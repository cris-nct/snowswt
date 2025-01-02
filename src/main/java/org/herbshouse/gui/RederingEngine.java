package org.herbshouse.gui;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class RederingEngine implements Runnable {

    public static final int FPS = 60;

    private final Canvas canvas;

    public RederingEngine(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void run() {
        canvas.redraw();
        canvas.update();
        if (!canvas.isDisposed()) {
            Display.getDefault().timerExec(1000 / FPS, this);
        }
    }
}
