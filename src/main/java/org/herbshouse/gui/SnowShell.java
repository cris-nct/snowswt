package org.herbshouse.gui;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herbshouse.SnowingApplication;
import org.herbshouse.controller.LogicController;
import org.herbshouse.controller.ViewController;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.enemies.EnemyGenerator;
import org.herbshouse.logic.snow.SnowGenerator;
import org.herbshouse.logic.snow.Snowflake;

/**
 * The provided code defines a graphical user interface (GUI) for a snow simulation application
 * using the SWT (Standard Widget Toolkit) framework in Java. The SnowShell class extends Shell and
 * implements PaintListener, allowing it to create a full-screen window that displays animated
 * snowflakes. Users can interact with the application using keyboard and mouse events to control
 * various features such as wind effects, image rotation, debugging options, and snowflake freezing.
 * The application continuously updates the display to simulate falling snowflakes.
 */
public class SnowShell extends Shell implements PaintListener, ViewController {

  private final Canvas canvas;
  private final List<IDrawCompleteListener> drawCompleteListeners = new ArrayList<>();
  private final List<String> videos = new ArrayList<>();
  private final ShutdownAnimation shutdownAnimation;
  private final Transform transform;

  private RenderingEngine renderingEngine;
  private SwtImageBuilder swtImageBuilder;
  private LogicController controller;
  private Region shellRegion;
  private Browser browser;
  private int videosIndex;
  private boolean startShutdown;

  public SnowShell(Transform transform) {
    super(Display.getDefault(), SWT.NO_TRIM);

    this.transform = transform;
    this.shellRegion = new Region(Display.getDefault());
    this.shellRegion.add(GuiUtils.SCREEN_BOUNDS);

    transform = new Transform(Display.getDefault());
    transform.scale(1, -1);
    transform.translate(0, -GuiUtils.SCREEN_BOUNDS.height);

    this.initVideos();

    this.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).create());

    this.canvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.FOCUSED);
    this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
    Display.getDefault().timerExec(300, () -> setFullScreen(true));
    this.canvas.addPaintListener(this);
    this.canvas.addMouseMoveListener(e -> controller.mouseMove(e.x, e.y));
    this.canvas.addMouseWheelListener(e -> controller.mouseScrolled(e.count));
    this.canvas.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        controller.mouseDown(e.button, e.x, e.y);
      }
    });
    this.canvas.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.character) {
          case ' ':
            controller.switchNormalWind();
            break;
          case 'X':
          case 'x':
            controller.switchHappyWind();
            break;
          case 'F':
          case 'f':
            controller.flipImage();
            break;
          case 'P':
          case 'p':
            controller.pause();
            break;
          case 'R':
          case 'r':
            controller.reset();
            break;
          case 'B':
          case 'b':
            controller.switchBigBalls();
            break;
          case 'D':
          case 'd':
            controller.switchDebug();
            break;
          case 'T':
          case 't':
            controller.switchObjectsTail();
            break;
          case 'A':
          case 'a':
            controller.switchAttack();
            break;
          case '1':
          case '2':
          case '3':
          case '4':
            controller.setAttackType(Integer.parseInt(String.valueOf(e.character)));
            break;
          case 'M':
          case 'm':
            controller.switchMercedesSnowflakes();
            break;
          case '+':
            controller.increaseSnowLevel();
            break;
          case '-':
            controller.decreaseSnowLevel();
            break;
          case 'y':
          case 'Y':
            controller.switchYoutube();
            updateBrowser(controller.getFlagsConfiguration().isYoutube());
            if (controller.getFlagsConfiguration().isYoutube()) {
              playNext();
            }
            break;
          case 'e':
          case 'E':
            controller.switchEnemies();
            break;
          case 'n':
          case 'N':
            if (browser != null) {
              playNext();
            }
            break;
          case 'q':
          case 'Q':
            resetScreenSurface();
            if (!controller.getFlagsConfiguration().isYoutube()) {
              updateBrowser(true);
            }
            browser.setVisible(false);
            browser.setText(loadResourceAsString("embededGoodBye.html"), true);
            canvas.removeKeyListener(this);
            controller.shutdown();
            startShutdown = true;
            break;
        }
      }
    });

    this.shutdownAnimation = new ShutdownAnimation(this);
    this.addDisposeListener(_ -> {
      if (!shellRegion.isDisposed()) {
        shellRegion.dispose();
      }
    });
  }

  public void setController(LogicController controller) {
    this.controller = controller;
    this.swtImageBuilder = new SwtImageBuilder(controller, transform);
    this.renderingEngine = new RenderingEngine(canvas, controller.getDesiredFps());
    this.registerListener(renderingEngine);
  }

  private void initVideos() {
    this.videos.add(loadResourceAsString("embededLetItSnow2.html"));
    this.videos.add(loadResourceAsString("embededLetItSnow.html"));
    this.videos.add(loadResourceAsString("embededChristmasMusic.html"));
    this.videos.add(loadResourceAsString("embededMusicSensual.html"));
  }

  private void updateBrowser(boolean youtubeOn) {
    if (!youtubeOn) {
      browser.dispose();
      browser = null;
      return;
    }
    browser = new Browser(canvas, SWT.EDGE | SWT.NO_FOCUS);
    int width = 400;
    int height = (int) (width / 1.77);
    int locX = (GuiUtils.SCREEN_BOUNDS.width - width) / 2;
    int locY = (GuiUtils.SCREEN_BOUNDS.height - height) / 2 + height;
    browser.setSize(width, height);
    browser.setLocation(locX, locY);
    browser.setEnabled(false);
  }

  private void playNext() {
    browser.setText(videos.get(videosIndex++ % videos.size()), true);
  }

  private String loadResourceAsString(String filename) {
    try {
      try (InputStream is = SnowingApplication.class.getClassLoader()
          .getResourceAsStream(filename)) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.copy(Objects.requireNonNull(is), baos);
        baos.close();
        return baos.toString(StandardCharsets.UTF_8);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void substractAreaFromShell(int[] polygon) {
    Display.getDefault().asyncExec(() -> {
      shellRegion.subtract(polygon);
      setRegion(shellRegion);
      setLocation(0, 0);
    });
  }

  @Override
  public void resetScreenSurface() {
    Display.getDefault().asyncExec(() -> {
      if (!shellRegion.isDisposed()) {
        shellRegion.dispose();
      }
      shellRegion = new Region(Display.getDefault());
      shellRegion.add(GuiUtils.SCREEN_BOUNDS);
      setRegion(shellRegion);
      setLocation(0, 0);
    });
  }

  @Override
  public void open() {
    Display.getDefault().timerExec(100, renderingEngine);
    super.open();
  }

  @Override
  public void paintControl(PaintEvent paintEvent) {
    GC gc = paintEvent.gc;
    //Draw background
    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
    gc.fillRectangle(GuiUtils.SCREEN_BOUNDS);

    try (var imageBuilder = swtImageBuilder.drawBaseElements()) {
      //Draw objects from each listener
      for (GeneratorListener<? extends AbstractMovableObject> listener : controller.getListeners()) {
        if (listener instanceof SnowGenerator) {
          //noinspection unchecked
          GeneratorListener<Snowflake> generatorListener = (GeneratorListener<Snowflake>) listener;
          imageBuilder.drawSnowflakes(generatorListener);
          imageBuilder.drawCountDown(generatorListener);
        } else if (listener instanceof EnemyGenerator) {
          //noinspection unchecked
          imageBuilder.drawEnemies((GeneratorListener<AbstractMovableObject>) listener);
        }
      }

      imageBuilder.addLegend(this.renderingEngine.getRealFPS(), controller.getCurrentAttackPhase());
      imageBuilder.addLogo();
      imageBuilder.addMinimap();
      Image image = imageBuilder.build();
      ImageData imageData = image.getImageData();
      if (startShutdown) {
        this.shutdownAnimation.draw(gc, image);
      } else {
        gc.drawImage(image, 0, 0);
      }
      for (GeneratorListener<?> generatorListener : controller.getListeners()) {
        generatorListener.provideImageData(imageData);
      }
    }

    drawCompleteListeners.forEach(IDrawCompleteListener::drawCompleted);
  }

  @Override
  protected void checkSubclass() {
  }

  public void registerListener(IDrawCompleteListener listener) {
    this.drawCompleteListeners.add(listener);
  }

}
