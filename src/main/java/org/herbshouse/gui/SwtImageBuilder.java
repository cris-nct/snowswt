package org.herbshouse.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.SnowingApplication;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.UserInfo;
import org.herbshouse.logic.enemies.AbstractEnemy;
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
    private static int alphaMB = 1;
    private static int alphaMBSign = 1;
    private final FlagsConfiguration config;
    private final UserInfo userInfo;

    SwtImageBuilder(FlagsConfiguration config, UserInfo userInfo) {
        this.config = config;
        this.userInfo = userInfo;
    }

    public SwtImageBuilder drawBaseElements(GC gc){
        if (gcImage != null){
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        Rectangle totalArea = Display.getDefault().getBounds();
        image = new Image(Display.getDefault(), totalArea);
        gcImage = new GC(image);
//        gcImage.setAdvanced(true);
//        gcImage.setAntialias(SWT.DEFAULT);
        gcImage.setTextAntialias(SWT.ON);
        gcImage.setTransform(config.getTransform());

        //Draw background
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gcImage.fillRectangle(0, 0, totalArea.width, totalArea.height);

        this.drawTextMiddleScreen();
        return this;
    }

    public Image build() {
        return image;
    }

    public void drawTextMiddleScreen() {
        if (gcImage == null){
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        //Draw text in middle of screen
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
        gcImage.setFont(SWTResourceManager.getFont("Arial", 25, SWT.BOLD));
        Point textSize = gcImage.stringExtent(TEXT_MIDDLE_SCREEN);
        gcImage.drawText(TEXT_MIDDLE_SCREEN, (Display.getDefault().getBounds().width - textSize.x) / 2,
                (Display.getDefault().getBounds().height - textSize.y) / 2, true);
    }

    public void drawCountDown(GeneratorListener<Snowflake> generatorListener) {
        if (gcImage == null){
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        //Draw countdown
        Rectangle drawingSurface = Display.getDefault().getBounds();
        if (generatorListener.getCountdown() >= 0) {
            if (generatorListener.getCountdown() >= 4) {
                gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            } else {
                gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            }
            String countdown = String.valueOf(generatorListener.getCountdown());
            Point countdownSize = gcImage.stringExtent(countdown);
            Point textSize = gcImage.stringExtent(TEXT_MIDDLE_SCREEN);
            gcImage.drawText(countdown, (drawingSurface.width - countdownSize.x) / 2,
                    drawingSurface.height / 2 + textSize.y, true);
        }
    }

    public SwtImageBuilder addLegend(int realFPS) {
        if (gcImage == null){
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
        this.addTextToLegend(legendBuilder, "Attack mode(A)", config.isAttack());
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
        this.addTextToLegend(legendBuilder, "Desired FPS", RenderingEngine.FPS);
        this.addTextToLegend(legendBuilder, "Real FPS", realFPS);
        legendBuilder.append("\nExit(Q)");
        gcImage.drawText(legendBuilder.toString(), Display.getDefault().getBounds().width - 240, 10, true);
        return this;
    }

    public SwtImageBuilder addLogo() {
        if (gcImage == null){
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }

        //Draw MB logo
        if (alphaMB >= 240 || alphaMB < 30) {
            alphaMBSign = -alphaMBSign;
            alphaMB = Math.min(alphaMB, 240);
            alphaMB = Math.max(alphaMB, 30);
        }
        gcImage.setAlpha(alphaMB);
        alphaMB = alphaMB + 5 * alphaMBSign;
        Image mbImage = SWTResourceManager.getImage(SnowingApplication.class, "mb.png", true);
        gcImage.drawImage(mbImage, 0, 0);
        gcImage.setAlpha(255);
        return this;
    }

    public SwtImageBuilder drawEnemies(GeneratorListener<AbstractEnemy> generatorListener) {
        if (gcImage == null){
            throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
        }
        gcImage.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gcImage.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
        for (AbstractEnemy obj : generatorListener.getMoveableObjects()) {
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
                int locX = (int) animatedGif.getLocation().x - obj.getSize() / 2;
                int locY = (int) animatedGif.getLocation().y - obj.getSize() / 2;
                gcImage.drawImage(img, locX, locY);
                animatedGif.increaseImageIndex();
            }
        }
        return this;
    }

    public SwtImageBuilder drawSnowflakes(GeneratorListener<Snowflake> generatorListener) {
        if (gcImage == null){
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
            if (config.isAttack() && !snowflake.isFreezed() && snowflakes.size() < 500) {
                gcImage.drawLine(
                        (int) snowflake.getLocation().x,
                        (int) snowflake.getLocation().y,
                        (int)config.getMouseLoc().x,
                        (int)config.getMouseLoc().y
                );
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

    @Override
    public void close() {
        gcImage.dispose();
        image.dispose();
        gcImage = null;
        image = null;
    }

}
