package org.herbshouse.gui;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.SnowGenerator;
import org.herbshouse.logic.SnowListener;
import org.herbshouse.logic.Snowflake;

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
    private final SnowGenerator snowGenerator;
    private final List<SnowListener> listeners = new ArrayList<>();
    private final FlagsConfiguration flagsConfiguration = new FlagsConfiguration();

    public SnowShell(SnowGenerator snowGenerator) {
        super(Display.getDefault());
        //Setting region for a shell works only with style SWT.NO_TRIM
//        Region region = new Region(Display.getDefault());
//        region.add(Display.getDefault().getBounds());
//        region.subtract(250, 30, 800, 200);
//        this.setRegion(region);

        this.snowGenerator = snowGenerator;
        this.snowGenerator.setFlagsConfiguration(flagsConfiguration);
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

        this.canvas.addMouseMoveListener(e ->
                listeners.forEach(l -> l.mouseMove(new Point2D(e.x, e.y)))
        );

        this.canvas.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseScrolled(MouseEvent e) {
                listeners.forEach(l -> l.mouseScrolled(e));
            }
        });
        this.canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {

            }

            @Override
            public void mouseDown(MouseEvent e) {
                listeners.forEach(l -> l.mouseDown(e));
            }

            @Override
            public void mouseUp(MouseEvent e) {

            }
        });
        this.canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.character){
                    case ' ':
                        flagsConfiguration.switchNormalWind();
                        break;
                    case 'X':
                    case 'x':
                        if (flagsConfiguration.isHappyWind()) {
                            listeners.forEach(SnowListener::turnOffHappyWind);
                        } else {
                            listeners.forEach(SnowListener::turnOnHappyWind);
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
                        listeners.forEach(l -> l.freezeSnowflakes(snowGenerator.getSnowflakes()));
                        break;
                    case 'R':
                    case 'r':
                        listeners.forEach(SnowListener::reset);
                        break;
                    case 'B':
                    case 'b':
                        flagsConfiguration.switchBigBalls();
                        break;
                    case 'D':
                    case 'd':
                        flagsConfiguration.switchDebug();
                        listeners.forEach(SnowListener::switchDebug);
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

    public void registerListener(SnowListener listener) {
        listeners.add(listener);
    }

    @Override
    public void open() {
        Display.getDefault().timerExec(100, new Runnable() {
            @Override
            public void run() {
                canvas.redraw();
                canvas.update();
                if (!isDisposed()) {
                    Display.getDefault().timerExec(1000 / SnowingApplication.FPS, this);
                }
            }
        });
        super.open();
    }

    @Override
    public void paintControl(PaintEvent paintEvent) {
        try (SwtImageBuilder imageBuilder = new SwtImageBuilder(paintEvent.gc)) {
            //Draw main image
            Image image = imageBuilder.createImage(snowGenerator, flagsConfiguration);
            ImageData imageData = image.getImageData();
            paintEvent.gc.drawImage(image, 0, 0);

            //Draw minimap
            double aspRatio = (double)imageData.width / imageData.height;
            int heightMinimap = 200;
            int widthMinimap = (int) (heightMinimap * aspRatio);
            paintEvent.gc.drawImage(image, 0, 0, imageData.width, imageData.height,
                    0, imageData.height - heightMinimap, widthMinimap, heightMinimap);
            paintEvent.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            paintEvent.gc.drawRectangle(0, imageData.height - heightMinimap, widthMinimap, heightMinimap - 1);

            //Check collisions for snowflakes and notify listeners
            List<Snowflake> toFreeze = new ArrayList<>();
            for (Snowflake snowflake : snowGenerator.getSnowflakes()) {
                if (isColliding(snowflake, imageData)) {
                    toFreeze.add(snowflake);
                }
            }
            if (!toFreeze.isEmpty()) {
                listeners.forEach(l -> l.freezeSnowflakes(toFreeze));
            }
        }
    }

    private boolean isColliding(Snowflake snowflake, ImageData imageData) {
        List<RGB> colors = new ArrayList<>();
        RGB pixelColorRight = GuiUtils.getPixelColor(imageData,
                (int) snowflake.getLocation().x + snowflake.getSize() / 2,
                (int) snowflake.getLocation().y
        );
        colors.add(pixelColorRight);

        for (int i = 0; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                RGB pixelColorBottom = GuiUtils.getPixelColor(imageData,
                        (int) snowflake.getLocation().x + j,
                        (int) snowflake.getLocation().y + snowflake.getSize() / 2 + i
                );
                colors.add(pixelColorBottom);
            }
        }
        return colors.stream().anyMatch(p -> p.equals(new RGB(255, 255, 0)));
    }


    @Override
    protected void checkSubclass() {
    }


}
