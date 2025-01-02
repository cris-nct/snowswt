package org.herbshouse.logic.enemies;

import org.eclipse.swt.graphics.RGB;
import org.herbshouse.logic.AbstractMovableObject;

public class AnimatedGif extends AbstractMovableObject {

    private double imageIndex;

    private final double animationSpeed;

    private final RGB removeBackgroundColor;

    private final String filename;

    public AnimatedGif(
            String filename,
            double animationSpeed,
            RGB removeBackgroundColor
    ) {
        this.filename = filename;
        this.animationSpeed = animationSpeed;
        this.removeBackgroundColor = removeBackgroundColor;
    }

    public String getFilename() {
        return filename;
    }

    public RGB getRemoveBackgroundColor() {
        return removeBackgroundColor;
    }

    public int getImageIndex() {
        return (int) imageIndex;
    }

    public void increaseImageIndex() {
        imageIndex += animationSpeed;
    }
}
