package org.herbshouse;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.gui.SnowShell;
import org.herbshouse.logic.redface.EnemyGenerator;
import org.herbshouse.logic.snow.SnowGenerator;

public class SnowingApplication {
    public static final int MB_ICON_SIZE = 25;

    public static Image mbImageSmall;

    public static void main(String[] args) {
        SnowGenerator generator = new SnowGenerator();
        EnemyGenerator enemyGenerator = new EnemyGenerator();
        try {
            mbImageSmall = new Image(Display.getDefault(),
                    SWTResourceManager.getImage(SnowingApplication.class, "../../mb.png", true)
                    .getImageData()
                    .scaledTo(MB_ICON_SIZE, MB_ICON_SIZE)
            );

            SnowShell shell = new SnowShell();
            shell.registerListener(generator);
            shell.registerListener(enemyGenerator);
            shell.open();
            generator.start();
            enemyGenerator.start();
            while (!shell.isDisposed()) {
                if (!Display.getDefault().readAndDispatch()) {
                    Display.getDefault().sleep();
                }
            }
        } finally {
            generator.shutdown();
            enemyGenerator.shutdown();
            SWTResourceManager.disposeAll();
            mbImageSmall.dispose();
        }
    }
}