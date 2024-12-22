package org.herbshouse.logic;

import org.eclipse.swt.events.MouseEvent;

import java.util.List;

public interface SnowListener {
    void turnOnHappyWind();

    void turnOffHappyWind();

    void freezeSnowflakes(List<Snowflake> snowflakes);

    void switchDebug();

    void mouseMove(Point2D mouseLocation);

    void mouseDown(MouseEvent mouseEvent);

    void mouseScrolled(MouseEvent mouseEvent);

    void reset();
}
