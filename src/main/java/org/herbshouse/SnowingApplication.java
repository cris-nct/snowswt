package org.herbshouse;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.gui.SnowShell;
import org.herbshouse.logic.SnowGenerator;

public class SnowingApplication {
    public static final int FPS = 120;

    public static final int MB_ICON_SIZE = 25;

    public static Image mbImageSmall;

    public static void main(String[] args) {
        SnowGenerator generator = new SnowGenerator(Display.getDefault().getBounds());
        try {
            mbImageSmall = new Image(Display.getDefault(),
                    SWTResourceManager.getImage(SnowingApplication.class, "../../mb.png", true)
                    .getImageData()
                    .scaledTo(MB_ICON_SIZE, MB_ICON_SIZE)
            );

            SnowShell shell = new SnowShell();
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
            mbImageSmall.dispose();
        }
    }
}