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
        SnowGenerator snowGenerator = new SnowGenerator();
        EnemyGenerator enemyGenerator = new EnemyGenerator();
        try {
            mbImageSmall = new Image(Display.getDefault(),
                    SWTResourceManager.getImage(SnowingApplication.class, "../../mb.png", true)
                    .getImageData()
                    .scaledTo(MB_ICON_SIZE, MB_ICON_SIZE)
            );

            SnowShell shell = new SnowShell();
            shell.registerListener(snowGenerator);
            shell.registerListener(enemyGenerator);
            enemyGenerator.registerListener(shell);
            shell.open();
            snowGenerator.start();
            enemyGenerator.start();
            while (!shell.isDisposed()) {
                if (!Display.getDefault().readAndDispatch()) {
                    Display.getDefault().sleep();
                }
            }
        } finally {
            snowGenerator.shutdown();
            enemyGenerator.shutdown();
            SWTResourceManager.disposeAll();
            mbImageSmall.dispose();
        }
    }
}