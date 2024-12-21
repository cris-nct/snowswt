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
import org.herbshouse.logic.SnowGenerator;
import org.herbshouse.logic.SnowListener;
import org.herbshouse.logic.Snowflake;

import java.util.ArrayList;
import java.util.List;

public class SnowShell extends Shell implements PaintListener{
    private final Canvas canvas;
    private final SnowGenerator snowGenerator;
    private boolean normalWind;
    private boolean happyWind;
    private boolean imageRotation;
    private final List<SnowListener> listeners = new ArrayList<>();
    private boolean debug;

    public SnowShell(SnowGenerator snowGenerator) {
        this.snowGenerator = snowGenerator;
        this.setFullScreen(true);
        this.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).create());

        this.canvas = new Canvas(this, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
        this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.canvas.addPaintListener(this);
        this.canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                //Display.getDefault().beep();
                setFullScreen(!getFullScreen());
            }
        });

        this.canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character == ' ') {
                    if (normalWind) {
                        listeners.forEach(SnowListener::turnOffNormalWind);
                    } else {
                        listeners.forEach(SnowListener::turnOnNormalWind);
                    }
                    normalWind = !normalWind;
                } else if (e.character == 'X' || e.character == 'x') {
                    if (happyWind) {
                        listeners.forEach(SnowListener::turnOffHappyWind);
                    } else {
                        listeners.forEach(SnowListener::turnOnHappyWind);
                    }
                    happyWind = !happyWind;
                } else if (e.character == 'F' || e.character == 'f') {
                    imageRotation = !imageRotation;
                } else if (e.character == 'P' || e.character == 'p') {
                    listeners.forEach(l -> l.freezeSnowflakes(snowGenerator.getSnowflakes()));
                } else if (e.character == 'B' || e.character == 'b') {
                    listeners.forEach(SnowListener::switchDisplayBigBalls);
                } else if (e.character == 'D' || e.character == 'd') {
                    debug = !debug;
                    listeners.forEach(SnowListener::switchDebug);
                } else if (e.character == 'H' || e.character == 'h') {
                    listeners.forEach(SnowListener::switchHeavySnowing);
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
            Image image = imageBuilder.createImage(snowGenerator, imageRotation, debug);
            paintEvent.gc.drawImage(image, 0, 0);

            ImageData imageData = image.getImageData();
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
