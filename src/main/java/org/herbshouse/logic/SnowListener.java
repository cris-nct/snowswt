package org.herbshouse.logic;

import org.eclipse.swt.events.MouseEvent;
import org.herbshouse.gui.FlagsConfiguration;

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

    void init(FlagsConfiguration flagsConfiguration);

    int getCountdown();

    List<Snowflake> getSnowflakes();
}
