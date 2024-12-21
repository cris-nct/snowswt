package org.herbshouse;

import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.gui.SnowShell;
import org.herbshouse.logic.SnowGenerator;

public class SnowingApplication {
    public static final boolean HEAVY_SNOWING = false;
    public static final boolean DEBUG_PATH = false;

    public static void main(String[] args) {
        SnowGenerator generator = new SnowGenerator(Display.getDefault().getBounds());
        try {
            generator.start();

            SnowShell shell = new SnowShell(generator);
            shell.registerListener(generator);
            shell.open();

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