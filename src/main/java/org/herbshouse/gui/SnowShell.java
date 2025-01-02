package org.herbshouse.gui;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.UserInfo;
import org.herbshouse.logic.enemies.AbstractEnemy;
import org.herbshouse.logic.enemies.EnemyGenerator;
import org.herbshouse.logic.snow.SnowGenerator;
import org.herbshouse.logic.snow.Snowflake;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * The provided code defines a graphical user interface (GUI) for a snow simulation application using the SWT
 * (Standard Widget Toolkit) framework in Java.
 * The SnowShell class extends Shell and implements PaintListener, allowing it to create a full-screen window
 * that displays animated snowflakes. Users can interact with the application using keyboard and mouse events
 * to control various features such as wind effects, image rotation, debugging options, and snowflake freezing.
 * The application continuously updates the display to simulate falling snowflakes.
 */
public class SnowShell extends Shell implements PaintListener, GuiListener {
    private final Canvas canvas;
    private final List<GeneratorListener<? extends AbstractMovableObject>> listeners = new ArrayList<>();
    private final FlagsConfiguration flagsConfiguration = new FlagsConfiguration();
    private final SwtImageBuilder swtImageBuilder;
    private Region shellRegion;
    private Browser browser;

    public SnowShell(UserInfo userInfo) {
        super(Display.getDefault(), SWT.NO_TRIM);
        this.swtImageBuilder = new SwtImageBuilder(flagsConfiguration, userInfo);
        this.shellRegion = new Region(Display.getDefault());
        this.shellRegion.add(Display.getDefault().getBounds());
        this.setFullScreen(true);
        this.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).create());

        this.canvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.FOCUSED);
        this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.canvas.addPaintListener(this);
        this.canvas.addMouseMoveListener(e -> {
                    flagsConfiguration.setMouseCurrentLocation(e.x, e.y);
                    listeners.forEach(l -> l.mouseMove(new Point2D(e.x, e.y)));
                }
        );
        this.canvas.addMouseWheelListener(e -> listeners.forEach(l -> l.mouseScrolled(e)));
        this.canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                listeners.forEach(l -> l.mouseDown(e));
            }
        });
        this.canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.character) {
                    case ' ':
                        flagsConfiguration.switchNormalWind();
                        break;
                    case 'X':
                    case 'x':
                        if (flagsConfiguration.isHappyWind()) {
                            listeners.forEach(GeneratorListener::turnOffHappyWind);
                        } else {
                            listeners.forEach(GeneratorListener::turnOnHappyWind);
                        }
                        flagsConfiguration.switchHappyWind();
                        break;
                    case 'F':
                    case 'f':
                        flagsConfiguration.switchFlipImage();
                        break;
                    case 'P':
                    case 'p':
                        flagsConfiguration.switchFreezeSnowflakes();
                        listeners.forEach(GeneratorListener::freezeMovableObjects);
                        break;
                    case 'R':
                    case 'r':
                        listeners.forEach(GeneratorListener::reset);
                        break;
                    case 'B':
                    case 'b':
                        flagsConfiguration.switchBigBalls();
                        break;
                    case 'D':
                    case 'd':
                        flagsConfiguration.switchDebug();
                        listeners.forEach(GeneratorListener::switchDebug);
                        break;
                    case 'A':
                    case 'a':
                        flagsConfiguration.switchAttack();
                        break;
                    case 'M':
                    case 'm':
                        flagsConfiguration.switchMercedesSnowflakes();
                        break;
                    case '+':
                        flagsConfiguration.increaseSnowingLevel();
                        break;
                    case '-':
                        flagsConfiguration.decreaseSnowingLevel();
                        break;
                    case 'l':
                    case 'L':
                        flagsConfiguration.switchMusic();
                        updateBrowser(flagsConfiguration.isMusic());
                        break;
                }
            }
        });
    }

    private void updateBrowser(boolean musicOn) {
        if (!musicOn) {
            browser.dispose();
            browser = null;
            return;
        }
        browser = new Browser(canvas, SWT.EDGE | SWT.NO_FOCUS);
        int width = 400;
        int height = (int) (width / 1.77);
        int locX = (Display.getDefault().getBounds().width - width) / 2;
        int locY = (Display.getDefault().getBounds().height - height) / 2 + height;
        browser.setSize(width, height);
        browser.setLocation(locX, locY);
        try {
            File file = new File(SnowingApplication.class.getResource("../../embededChristmasMusic.html").getFile());
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            browser.setText(content, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Display.getDefault().timerExec(1000, () ->
                {
                    GuiUtils.moveMouseAndClick(
                            locX + width / 2,
                            locY + height / 2,
                            flagsConfiguration.getMouseLocX(),
                            flagsConfiguration.getMouseLocY()

                    );
                    browser.setEnabled(false);
                }
        );
    }

    @Override
    public void substractAreaFromShell(int[] polygon) {
        this.shellRegion.subtract(polygon);
        this.setRegion(shellRegion);
        this.setLocation(0, 0);
    }

    @Override
    public void resetShellSurface() {
        if (!shellRegion.isDisposed()) {
            shellRegion.dispose();
        }
        this.shellRegion = new Region(Display.getDefault());
        this.shellRegion.add(Display.getDefault().getBounds());
        this.setRegion(shellRegion);
        this.setLocation(0, 0);
    }

    public void registerListener(GeneratorListener<?> listener) {
        listener.init(flagsConfiguration, Display.getDefault().getBounds());
        listeners.add(listener);
    }

    @Override
    public void open() {
        Display.getDefault().timerExec(100, new RenderingEngine(canvas));
        super.open();
    }

    @Override
    public void paintControl(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        try (var imageBuilder = swtImageBuilder.drawBaseElements(gc)) {
            //Draw objects from each listener
            for (GeneratorListener<? extends AbstractMovableObject> listener : listeners) {
                if (listener instanceof SnowGenerator) {
                    //noinspection unchecked
                    GeneratorListener<Snowflake> generatorListener = (GeneratorListener<Snowflake>) listener;
                    imageBuilder.drawSnowflakes(generatorListener);
                    imageBuilder.drawCountDown(generatorListener);
                } else if (listener instanceof EnemyGenerator) {
                    //noinspection unchecked
                    imageBuilder.drawEnemies((GeneratorListener<AbstractEnemy>) listener);
                }
            }

            Image image = imageBuilder.addLegend().addLogo().build();
            ImageData imageData = image.getImageData();
            gc.drawImage(image, 0, 0);

            //Draw minimap
            this.drawMinimap(gc, image, imageData);

            //Check collisions for snowflakes and notify listeners
            for (GeneratorListener<?> generatorListener : listeners) {
                generatorListener.checkCollisions(imageData);
            }
        }
    }

    private void drawMinimap(GC gc, Image image, ImageData imageData) {
        //Draw minimap
        double aspRatio = (double) imageData.width / imageData.height;
        int heightMinimap = 200;
        int widthMinimap = (int) (heightMinimap * aspRatio);
        gc.setAlpha(180);
        gc.drawImage(image, 0, 0, imageData.width, imageData.height,
                0, imageData.height - heightMinimap, widthMinimap, heightMinimap);
        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        gc.drawRectangle(0, imageData.height - heightMinimap, widthMinimap, heightMinimap - 1);
        gc.setAlpha(255);
    }

    @Override
    protected void checkSubclass() {
    }

}
