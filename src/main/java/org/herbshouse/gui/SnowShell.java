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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
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
import org.herbshouse.gui.imageBuilder.SwtImageBuilder;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.enemies.EnemyGenerator;
import org.herbshouse.logic.fractals.FractalsGenerator;
import org.herbshouse.logic.fractals.Tree;
import org.herbshouse.logic.fractals.TreeType;
import org.herbshouse.logic.snow.SnowGenerator;
import org.herbshouse.logic.snow.Snowflake;

/**
 * The provided code defines a graphical user interface (GUI) for a snow simulation application using the SWT (Standard Widget Toolkit) framework in
 * Java. The SnowShell class extends Shell and implements PaintListener, allowing it to create a full-screen window that displays animated snowflakes.
 * Users can interact with the application using keyboard and mouse events to control various features such as wind effects, image rotation, debugging
 * options, and snowflake freezing. The application continuously updates the display to simulate falling snowflakes.
 */
public class SnowShell extends Shell implements
    PaintListener, MouseListener, MouseMoveListener, MouseWheelListener, KeyListener,
    ViewController {

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

    this.initVideos();

    this.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).create());

    this.canvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.FOCUSED);
    this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
    this.canvas.addPaintListener(this);
    this.canvas.addMouseMoveListener(this);
    this.canvas.addMouseWheelListener(this);
    this.canvas.addMouseListener(this);
    this.canvas.addKeyListener(this);

    Display.getDefault().timerExec(300, () -> setFullScreen(true));

    this.shutdownAnimation = new ShutdownAnimation(this);
    this.addDisposeListener(_ -> {
      if (!shellRegion.isDisposed()) {
        shellRegion.dispose();
      }
      swtImageBuilder.shutdown();
    });
  }

  public void setController(LogicController controller) {
    this.controller = controller;
    this.swtImageBuilder = new SwtImageBuilder(controller, transform);
    this.renderingEngine = new RenderingEngine(canvas, controller.getDesiredFps());
    this.registerListener(renderingEngine);
  }

  private void initVideos() {
    this.videos.add(loadResourceAsString("embeded-let-it-snow-2.html"));
    this.videos.add(loadResourceAsString("embeded-let-it-snow.html"));
    this.videos.add(loadResourceAsString("embeded-christmas-music.html"));
    this.videos.add(loadResourceAsString("embeded-music-sensual.html"));
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
        } else if (listener instanceof FractalsGenerator) {
          //noinspection unchecked
          imageBuilder.drawFractals((GeneratorListener<Tree>) listener);
        }
      }

      imageBuilder.drawText();
      imageBuilder.drawLegend(this.renderingEngine.getRealFPS(),
          controller.getCurrentAttackPhase());
      imageBuilder.drawLogo();
      imageBuilder.drawMinimap();
      Image image = imageBuilder.getResultedImage();
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

  @Override
  public void mouseDoubleClick(MouseEvent e) {

  }

  @Override
  public void mouseDown(MouseEvent e) {
    controller.mouseDown(e.button, e.x, e.y);
  }

  @Override
  public void mouseUp(MouseEvent e) {

  }

  @Override
  public void mouseMove(MouseEvent e) {
    controller.mouseMove(e.x, e.y);
  }

  @Override
  public void mouseScrolled(MouseEvent e) {
    controller.mouseScrolled(e.count);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.keyCode) {
      //Default
      case SWT.F1 -> controller.setFractalsType(TreeType.PERFECT_DEFAULT);
      //Fir
      case SWT.F2 -> controller.setFractalsType(TreeType.PERFECT_FIR);
      //Random default
      case SWT.F3 -> controller.setFractalsType(TreeType.RANDOM_DEFAULT);
      //Random fir
      case SWT.F4 -> controller.setFractalsType(TreeType.RANDOM_FIR);
    }
    switch (e.character) {
      case ' ':
        controller.switchNormalWind();
        break;
      case 'X':
      case 'x':
        controller.switchHappyWind();
        break;
      case 'L':
      case 'l':
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
      case '5':
        controller.setAttackType(Integer.parseInt(String.valueOf(e.character)));
        break;
      case 'H':
      case 'h':
        controller.switchBlackHoles();
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
      case 'f':
      case 'F':
        controller.switchFractals();
        break;
      case 'q':
      case 'Q':
        resetScreenSurface();
        if (!controller.getFlagsConfiguration().isYoutube()) {
          updateBrowser(true);
        }
        browser.setVisible(false);
        browser.setText(loadResourceAsString("embeded-goodbye.html"), true);
        canvas.removeKeyListener(this);
        canvas.removeMouseListener(this);
        canvas.removeMouseWheelListener(this);
        controller.shutdown();
        startShutdown = true;
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }
}
