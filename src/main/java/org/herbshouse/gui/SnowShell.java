package org.herbshouse.gui;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.redface.AbstractEnemy;
import org.herbshouse.logic.redface.EnemyGenerator;
import org.herbshouse.logic.snow.SnowGenerator;
import org.herbshouse.logic.snow.Snowflake;

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
public class SnowShell extends Shell implements PaintListener {
    private final Canvas canvas;
    private final List<GeneratorListener<? extends AbstractMovableObject>> listeners = new ArrayList<>();
    private final FlagsConfiguration flagsConfiguration = new FlagsConfiguration();

    public SnowShell() {
        super(Display.getDefault());
        //Setting region for a shell works only with style SWT.NO_TRIM
//        Region region = new Region(Display.getDefault());
//        region.add(Display.getDefault().getBounds());
//        region.subtract(250, 30, 800, 200);
//        this.setRegion(region);

        this.setFullScreen(true);
        this.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).create());

        this.canvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
        this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.canvas.addPaintListener(this);
        this.canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                //Display.getDefault().beep();
                if (e.button == 1) {
                    setFullScreen(!getFullScreen());
                }
            }
        });
        this.canvas.addMouseMoveListener(e -> {
                flagsConfiguration.setMouseCurrentLocation(e.x, e.y);
                listeners.forEach(l -> l.mouseMove(new Point2D(e.x, e.y)));
            }
        );
        this.canvas.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseScrolled(MouseEvent e) {
                listeners.forEach(l -> l.mouseScrolled(e));
            }
        });
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
                }
            }
        });
    }

    public void registerListener(GeneratorListener<?> listener) {
        listener.init(flagsConfiguration, Display.getDefault().getBounds());
        listeners.add(listener);
    }

    @Override
    public void open() {
        Display.getDefault().timerExec(100, new RederingEngine(canvas));
        super.open();
    }

    @Override
    public void paintControl(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        try (SwtImageBuilder imageBuilder = new SwtImageBuilder(gc, flagsConfiguration)) {
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

    private void drawMinimap(GC gc, Image image, ImageData imageData){
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
