package org.herbshouse.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.*;
import org.herbshouse.logic.enemies.AnimatedGif;
import org.herbshouse.logic.enemies.RedFace;
import org.herbshouse.logic.snow.Snowflake;

import java.util.List;

/**
 * This class is responsible for creating and managing an SWT Image that displays a snowy scene with a greeting text.
 * It utilizes the SWT graphics context (GC) to draw on an image, including a background, text, and snowflakes generated by a SnowGenerator.
 * The class supports flipping the image vertically and includes a debug mode to show the history of snowflake locations.
 * It implements AutoCloseable to ensure proper resource management, disposing of the graphics context, image, and transformation when done.
 *
 * @author cristian.tone
 */
public class SwtImageBuilder implements AutoCloseable {
    public static final String TEXT_MIDDLE_SCREEN = "Happy New Year!";

    private GC gcImage;
    private Image image;
    private final FlagsConfiguration config;
    private final UserInfo userInfo;
    private int counterLogo;

    SwtImageBuilder(FlagsConfiguration config, UserInfo userInfo) {
        this.config = config;
        this.userInfo = userInfo;
    }

    public SwtImageBuilder drawBaseElements() {
        if (gcImage != null) {
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        image = new Image(Display.getDefault(), GuiUtils.SCREEN_BOUNDS);
        gcImage = new GC(image);
//        gcImage.setAdvanced(true);
//        gcImage.setAntialias(SWT.DEFAULT);
        gcImage.setTextAntialias(SWT.ON);
        gcImage.setTransform(config.getTransform());

        //Draw background
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gcImage.fillRectangle(GuiUtils.SCREEN_BOUNDS);

        this.drawTextMiddleScreen();
        return this;
    }

    public Image build() {
        return image;
    }

    public void drawTextMiddleScreen() {
        if (gcImage == null) {
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        //Draw text in middle of screen
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
        gcImage.setFont(SWTResourceManager.getFont("Arial", 25, SWT.BOLD));
        Point textSize = gcImage.stringExtent(TEXT_MIDDLE_SCREEN);
        gcImage.drawText(TEXT_MIDDLE_SCREEN,
                (GuiUtils.SCREEN_BOUNDS.width - textSize.x) / 2,
                (GuiUtils.SCREEN_BOUNDS.height - textSize.y) / 2,
                true
        );
    }

    public void drawCountDown(GeneratorListener<Snowflake> generatorListener) {
        if (gcImage == null) {
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        //Draw countdown
        if (generatorListener.getCountdown() >= 0) {
            if (generatorListener.getCountdown() >= 4) {
                gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            } else {
                gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            }
            String countdown = String.valueOf(generatorListener.getCountdown());
            Point countdownSize = gcImage.stringExtent(countdown);
            Point textSize = gcImage.stringExtent(TEXT_MIDDLE_SCREEN);
            gcImage.drawText(countdown, (GuiUtils.SCREEN_BOUNDS.width - countdownSize.x) / 2,
                    GuiUtils.SCREEN_BOUNDS.height / 2 + textSize.y, true);
        }
    }

    public SwtImageBuilder addLegend(int realFPS) {
        if (gcImage == null) {
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        //Draw legend
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
        gcImage.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
        StringBuilder legendBuilder = new StringBuilder();
        this.addTextToLegend(legendBuilder, "Normal wind(space)", config.isNormalWind());
        this.addTextToLegend(legendBuilder, "Happy wind(X)", config.isHappyWind());
        this.addTextToLegend(legendBuilder, "Debug(D)", config.isDebug());
        this.addTextToLegend(legendBuilder, "Flip image(F)", config.isFlipImage());
        this.addTextToLegend(legendBuilder, "Big balls(B)", config.isBigBalls());
        this.addTextToLegend(legendBuilder, "Freeze snowflakes(P)", config.isFreezeSnowflakes());
        this.addTextToLegend(legendBuilder, "Attack mode (A)", config.isAttack());
        if (config.isAttack()) {
            legendBuilder.append(", type: ");
            legendBuilder.append(config.getAttackType());
        } else {
            legendBuilder.append("\nAttack types: 1-2");
        }
        this.addTextToLegend(legendBuilder, "Mercedes snowflakes(M)", config.isMercedesSnowflakes());
        this.addTextToLegend(legendBuilder, "Snow level(+/-)", config.getSnowingLevel());
        this.addTextToLegend(legendBuilder, "Enemies(E)", config.isEnemies());
        this.addTextToLegend(legendBuilder, "Youtube(Y)", config.isYoutube());
        if (config.isYoutube()) {
            legendBuilder.append(", Next video(N)");
        }
        legendBuilder.append("\n-------");
        this.addTextToLegend(legendBuilder, "Your points", userInfo.getPoints());
        legendBuilder.append("\n-------\n");
        legendBuilder.append("Fire(left button)\n");
        legendBuilder.append("Reset simulation(R)\n");
        legendBuilder.append("-------");
        this.addTextToLegend(legendBuilder, "Desired FPS", FlagsConfiguration.DESIRED_FPS);
        this.addTextToLegend(legendBuilder, "Real FPS", realFPS);
        legendBuilder.append("\nExit(Q)");
        gcImage.drawText(legendBuilder.toString(), GuiUtils.SCREEN_BOUNDS.width - 240, 10, true);
        return this;
    }

    public SwtImageBuilder addLogo() {
        if (gcImage == null) {
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        int alphaMB = (int) Utils.linearInterpolation(Math.sin(Math.toRadians((counterLogo += 5) % 360)),
                -1, 30, 1, 240
        );
        gcImage.setAlpha(alphaMB);
        Image mbImage = SWTResourceManager.getImage(SnowingApplication.class, "mb.png", true);
        gcImage.drawImage(mbImage, 0, 0);
        gcImage.setAlpha(255);
        return this;
    }

    public SwtImageBuilder drawEnemies(GeneratorListener<AbstractMovableObject> generatorListener) {
        if (gcImage == null) {
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
        for (AbstractMovableObject obj : generatorListener.getMoveableObjects()) {
            if (obj instanceof RedFace redFace) {
                GuiUtils.drawRedFace(gcImage, redFace);
            } else if (obj instanceof AnimatedGif animatedGif) {
                Image img = SWTResourceManager.getGif(SnowingApplication.class,
                        animatedGif.getFilename(),
                        animatedGif.getImageIndex(),
                        obj.getSize(),
                        obj.getSize(),
                        animatedGif.getRemoveBackgroundColor(),
                        true
                );

                Point screenLoc = GuiUtils.toScreenCoord(animatedGif.getLocation());
                screenLoc.x -= obj.getSize() / 2;
                screenLoc.y -= obj.getSize() / 2;
                gcImage.drawImage(img, screenLoc.x, screenLoc.y);
                animatedGif.increaseImageIndex();
            }
        }
        return this;
    }

    public SwtImageBuilder drawSnowflakes(GeneratorListener<Snowflake> generatorListener) {
        if (gcImage == null) {
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        //Draw snowflakes
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
        List<Snowflake> snowflakes = generatorListener.getMoveableObjects();
        for (Snowflake snowflake : snowflakes) {
            if (config.isMercedesSnowflakes()) {
                GuiUtils.drawSnowflakeAsMercedes(gcImage, snowflake);
            } else {
                GuiUtils.draw(gcImage, snowflake);
            }
            if (config.isDebug()) {
                for (Point2D loc : snowflake.getHistoryLocations()) {
                    GuiUtils.draw(gcImage, snowflake, loc);
                }
            }
        }
        return this;
    }

    private void addTextToLegend(StringBuilder builder, String text, boolean value) {
        if (!builder.isEmpty()) {
            builder.append("\r\n");
        }
        builder.append(text);
        builder.append(": ");
        builder.append(value ? "ON" : "OFF");
    }

    private void addTextToLegend(StringBuilder builder, String text, int value) {
        if (!builder.isEmpty()) {
            builder.append("\r\n");
        }
        builder.append(text);
        builder.append(": ");
        builder.append(value);
    }

    public SwtImageBuilder addMinimap() {
        //Draw minimap
        ImageData imageData = image.getImageData();
        double aspRatio = (double) imageData.width / imageData.height;
        int heightMinimap = 200;
        int widthMinimap = (int) (heightMinimap * aspRatio);
        gcImage.setAlpha(180);
        gcImage.drawImage(image, 0, 0, imageData.width, imageData.height,
                0, imageData.height - heightMinimap, widthMinimap, heightMinimap);
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        gcImage.drawRectangle(0, imageData.height - heightMinimap, widthMinimap, heightMinimap - 1);
        gcImage.setAlpha(255);
        return this;
    }

    @Override
    public void close() {
        gcImage.dispose();
        image.dispose();
        gcImage = null;
        image = null;
    }

}
