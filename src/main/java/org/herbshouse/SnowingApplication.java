package org.herbshouse;

import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.gui.SnowShell;
import org.herbshouse.logic.SnowGenerator;

public class SnowingApplication {
    public static final int FPS = 120;

    public static void main(String[] args) {
        SnowGenerator generator = new SnowGenerator(Display.getDefault().getBounds());
        try {
            SnowShell shell = new SnowShell(generator);
            shell.registerListener(generator);
            shell.open();
            generator.start();
            while (!shell.isDisposed()) {
                if (!Display.getDefault().readAndDispatch()) {
                    Display.getDefault().sleep();
                }
            }
        } finally {
            generator.shutdown();
            SWTResourceManager.disposeAll();
        }
    }
}