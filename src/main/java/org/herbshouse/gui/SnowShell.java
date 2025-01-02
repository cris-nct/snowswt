package org.herbshouse.gui;

import com.google.common.io.ByteStreams;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.UserInfo;
import org.herbshouse.logic.enemies.EnemyGenerator;
import org.herbshouse.logic.snow.SnowGenerator;
import org.herbshouse.logic.snow.Snowflake;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    private final List<IDrawCompleteListener> drawCompleteListeners = new ArrayList<>();
    private final FlagsConfiguration flagsConfiguration = new FlagsConfiguration();
    private final SwtImageBuilder swtImageBuilder;
    private Region shellRegion;
    private Browser browser;
    private final List<String> videos = new ArrayList<>();
    private int videosIndex;
    private final List<Transform> transforms = new ArrayList<>();
    private final RenderingEngine renderingEngine;
    private int currentTransformIndex = 0;

    public SnowShell(UserInfo userInfo) {
        super(Display.getDefault(), SWT.NO_TRIM);
        this.swtImageBuilder = new SwtImageBuilder(flagsConfiguration, userInfo);
        this.shellRegion = new Region(Display.getDefault());
        this.shellRegion.add(Display.getDefault().getBounds());

        this.initVideos();
        this.initTransforms();

        this.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).create());

        this.canvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.FOCUSED);
        this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
        Display.getDefault().timerExec(300, () -> setFullScreen(true));
        this.canvas.addPaintListener(this);
        this.canvas.addMouseMoveListener(e -> {
                    final Point2D mouseLoc = convertLoc(e.x, e.y);
                    flagsConfiguration.setMouseCurrentLocation(mouseLoc);
                    listeners.forEach(l -> l.mouseMove(mouseLoc));
                }
        );
        this.canvas.addMouseWheelListener(e -> listeners.forEach(l -> l.mouseScrolled(e)));
        this.canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                final Point2D mouseLoc = convertLoc(e.x, e.y);
                listeners.forEach(l -> l.mouseDown(e.button, mouseLoc));
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
                        if (flagsConfiguration.isFlipImage()) {
                            flagsConfiguration.setTransform(null);
                        } else {
                            flagsConfiguration.setTransform(transforms.get(currentTransformIndex));
                        }
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
                        if (flagsConfiguration.getSnowingLevel() == 0){
                            listeners.forEach(GeneratorListener::turnOffSnowing);
                        }
                        break;
                    case '-':
                        flagsConfiguration.decreaseSnowingLevel();
                        if (flagsConfiguration.getSnowingLevel() == 0){
                            listeners.forEach(GeneratorListener::turnOffSnowing);
                        }
                        break;
                    case 'y':
                    case 'Y':
                        flagsConfiguration.switchYoutube();
                        updateBrowser(flagsConfiguration.isYoutube());
                        break;
                    case 'e':
                    case 'E':
                        flagsConfiguration.switchEnemies();
                        break;
                    case 'n':
                    case 'N':
                        if (browser != null) {
                            playNext();
                        }
                        break;
                    case 'q':
                    case 'Q':
                        getShell().dispose();
                        break;
                }
            }
        });

        this.renderingEngine = new RenderingEngine(canvas);
        this.registerListener(renderingEngine);

        this.addDisposeListener(e -> {
            for (Transform transform : transforms) {
                if (!transform.isDisposed()) {
                    transform.dispose();
                }
            }
            transforms.clear();
        });
    }

    private Point2D convertLoc(int x, int y) {
        int locX = x;
        int locY = y;
        if (flagsConfiguration.isFlipImage()) {
            float[] data = {locX, locY};
            transforms.get(currentTransformIndex).transform(data);
            locX = (int) data[0];
            locY = (int) data[1];
        }
        return new Point2D(locX, locY);
    }

    private void initTransforms() {
        Transform transform = new Transform(Display.getDefault());
        transform.scale(1, -1);
        transform.translate(0, -Display.getDefault().getBounds().height);
        transforms.add(transform);
    }

    private void initVideos() {
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
        int locX = (Display.getDefault().getBounds().width - width) / 2;
        int locY = (Display.getDefault().getBounds().height - height) / 2 + height;
        browser.setSize(width, height);
        browser.setLocation(locX, locY);
        browser.setEnabled(false);
        this.playNext();
    }

    private void playNext() {
        browser.setText(videos.get(videosIndex++ % videos.size()), true);
    }

    private String loadResourceAsString(String filename) {
        try {
            try (InputStream is = SnowingApplication.class.getClassLoader().getResourceAsStream(filename)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ByteStreams.copy(is, baos);
                baos.close();
                return baos.toString(StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        Display.getDefault().timerExec(100, renderingEngine);
        super.open();
    }

    @Override
    public void paintControl(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        try (var imageBuilder = swtImageBuilder.drawBaseElements()) {
            //Draw objects from each listener
            for (GeneratorListener<? extends AbstractMovableObject> listener : listeners) {
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

            imageBuilder.addLegend(this.renderingEngine.getRealFPS());
            imageBuilder.addLogo();
            imageBuilder.addMinimap();
            Image image = imageBuilder.build();
            ImageData imageData = image.getImageData();
            gc.drawImage(image, 0, 0);

            //Check collisions for snowflakes and notify listeners
            for (GeneratorListener<?> generatorListener : listeners) {
                generatorListener.checkCollisions(imageData);
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
