package org.herbshouse.logic;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.gui.FlagsConfiguration;

import java.util.List;

public interface SnowListener<T extends AbstractMovableObject> {
    void turnOnHappyWind();

    void turnOffHappyWind();

    void freezeSnowflakes();

    void switchDebug();

    void mouseMove(Point2D mouseLocation);

    void mouseDown(MouseEvent mouseEvent);

    void mouseScrolled(MouseEvent mouseEvent);

    void reset();

    void init(FlagsConfiguration flagsConfiguration, Rectangle screenBounds);

    default int getCountdown(){
        return -1;
    }

    List<T> getSnowflakes();

    void shutdown();

    void checkCollisions(ImageData imageData);

}
