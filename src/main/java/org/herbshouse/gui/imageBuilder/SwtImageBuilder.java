package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.controller.LogicController;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.gui.SWTResourceManager;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.GraphicalImageGenerator;
import org.herbshouse.logic.graphicalSounds.GraphicalSoundsGenerator;
import org.herbshouse.logic.snow.Snowflake;

public class SwtImageBuilder implements AutoCloseable {

  private static final String TEXT_MIDDLE_SCREEN = "Happy New Year!";

  private final LogicController controller;
  private final Transform transform;
  private final LegendDrawer legendDrawer;
  private final TextDrawer textDrawer;
  private final CountdownDrawer countdownDrawer;
  private final SnowflakesDrawer snowflakesDrawer;
  private final EnemyDrawer enemyDrawer;
  private final LogoDrawer logoDrawer;
  private final MinimapDrawer minimapDrawer;
  private final SoundsDrawer soundsDrawer;
  private GC gcImage;
  private Image image;

  public SwtImageBuilder(LogicController controller, Transform transform) {
    this.controller = controller;
    this.transform = transform;
    this.legendDrawer = new LegendDrawer(controller);
    this.textDrawer = new TextDrawer();
    this.countdownDrawer = new CountdownDrawer();
    this.snowflakesDrawer = new SnowflakesDrawer(controller);
    this.enemyDrawer = new EnemyDrawer();
    this.logoDrawer = new LogoDrawer();
    this.minimapDrawer = new MinimapDrawer();
    this.soundsDrawer = new SoundsDrawer();
  }

  public SwtImageBuilder drawBaseElements() {
    initializeGraphics();
    return this;
  }

  public Image getResultedImage() {
    return image;
  }

  private void initializeGraphics() {
    if (gcImage != null) {
      throw new IllegalStateException("Graphics context already initialized.");
    }
    PaletteData palette = new PaletteData(0xFF0000, 0x00FF00, 0x0000FF);
    ImageData imageData = new ImageData(GuiUtils.SCREEN_BOUNDS.width, GuiUtils.SCREEN_BOUNDS.height, 24, palette);
    image = new Image(Display.getDefault(), imageData);
    gcImage = new GC(image);
    gcImage.setTextAntialias(SWT.ON);
    if (controller.getFlagsConfiguration().isFlipImage()) {
      gcImage.setTransform(transform);
    }
  }

  public void drawText() {
    textDrawer.drawCenteredText(gcImage, TEXT_MIDDLE_SCREEN,
        SWTResourceManager.getFont("Arial", 25, SWT.BOLD),
        SWT.COLOR_CYAN
    );
  }

  public void drawCountDown(GeneratorListener<Snowflake> generatorListener) {
    countdownDrawer.draw(gcImage, generatorListener, TEXT_MIDDLE_SCREEN);
  }

  public void drawLegend(int realFPS, int currentAttackPhase) {
    legendDrawer.draw(gcImage, realFPS, currentAttackPhase);
  }

  public void drawEnemies(GeneratorListener<AbstractMovableObject> generatorListener) {
    enemyDrawer.draw(gcImage, generatorListener);
  }

  public void drawSounds(GraphicalSoundsGenerator generatorListener) {
    soundsDrawer.draw(gcImage, generatorListener);
  }

  public void drawSnowflakes(GeneratorListener<Snowflake> generatorListener) {
    snowflakesDrawer.draw(gcImage, generatorListener);
  }

  public void drawImage(GraphicalImageGenerator generatorListener) {
    if (generatorListener.getImage() != null && !generatorListener.getImage().isDisposed()) {
      gcImage.drawImage(generatorListener.getImage(), 0, 0);
    }
  }

  public void drawLogo() {
    logoDrawer.draw(gcImage);
  }

  public void drawMinimap() {
    minimapDrawer.draw(gcImage, image);
  }

  @Override
  public void close() {
    if (gcImage != null) {
      gcImage.dispose();
      gcImage = null;
    }
    if (image != null) {
      image.dispose();
      image = null;
    }
  }

  public void shutdown() {

  }
}