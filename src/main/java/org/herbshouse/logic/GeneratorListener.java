package org.herbshouse.logic;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.AbstractController;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.gui.GuiListener;

import java.util.List;

public interface GeneratorListener<T extends AbstractMovableObject> {
    void turnOnHappyWind();

    void freezeMovableObjects();

    void switchDebug();

    void mouseMove(Point2D mouseLocation);

    void mouseDown(int button, Point2D mouseLocation);

    void mouseScrolled(int count);

    void reset();

    void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds);

    default int getCountdown() {
        return -1;
    }

    List<T> getMoveableObjects();

    void shutdown();

    void provideImageData(ImageData imageData);

    void switchAttack();

    default void changedSnowingLevel() {
    }

    void setController(AbstractController controller);

}
