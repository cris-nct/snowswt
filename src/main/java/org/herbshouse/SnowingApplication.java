package org.herbshouse;

import java.util.Arrays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.audio.DefaultAudioPlayer;
import org.herbshouse.controller.DefaultControllerImpl;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.gui.SnowShell;
import org.herbshouse.logic.UserInfo;
import org.herbshouse.logic.enemies.EnemyGenerator;
import org.herbshouse.logic.fractals.FractalsGenerator;
import org.herbshouse.logic.graphicalSounds.GraphicalSoundsGenerator;
import org.herbshouse.logic.snow.SnowGenerator;

public class SnowingApplication {

  public static final int MB_ICON_SIZE = 40;

  public static Image mbImageSmall;

  public static void main(String[] args) {
    boolean skipAnimation = Arrays.asList(args).contains("-skipInitialAnimation");

    SnowGenerator snowGenerator = new SnowGenerator();
    if (skipAnimation) {
      snowGenerator.skipInitialAnimation();
    }
    EnemyGenerator enemyGenerator = new EnemyGenerator();
    FractalsGenerator fractalsGenerator = new FractalsGenerator();
    GraphicalSoundsGenerator graphicalSoundsGenerator = new GraphicalSoundsGenerator();

    Transform transform = null;
    DefaultAudioPlayer audioPlayer = new DefaultAudioPlayer();
    try {
      mbImageSmall =
          SWTResourceManager.getSVG(SnowingApplication.class, "pictures/mb.svg", MB_ICON_SIZE, MB_ICON_SIZE, true);

      transform = new Transform(Display.getDefault());
      transform.scale(1, -1);
      transform.translate(0, -GuiUtils.SCREEN_BOUNDS.height);

      SnowShell shell = new SnowShell(transform);
      enemyGenerator.setViewController(shell);

      DefaultControllerImpl controller = new DefaultControllerImpl();
      controller.setDesiredFPS(120);
      controller.setUserInfo(new UserInfo());
      controller.setTransform(transform);
      controller.setAudio(audioPlayer);

      controller.registerListener(fractalsGenerator);
      controller.registerListener(snowGenerator);
      controller.registerListener(enemyGenerator);
      controller.registerListener(graphicalSoundsGenerator);

      shell.setController(controller);
      shell.open();

      snowGenerator.start();
      enemyGenerator.start();
      fractalsGenerator.start();
      graphicalSoundsGenerator.start();

      while (!shell.isDisposed()) {
        if (!Display.getDefault().readAndDispatch()) {
          Display.getDefault().sleep();
        }
      }
    } finally {
      if (transform != null) {
        transform.dispose();
      }
      snowGenerator.shutdown();
      enemyGenerator.shutdown();
      fractalsGenerator.shutdown();
      graphicalSoundsGenerator.shutdown();
      SWTResourceManager.disposeAll();
      mbImageSmall.dispose();
    }
  }
}