package org.herbshouse.logic;

import java.util.List;

public interface SnowListener {
    void turnOnHappyWind();

    void turnOffHappyWind();

    void freezeSnowflakes(List<Snowflake> snowflakes);

    void switchDebug();

    void mouseMove(Point2D mouseLocation);

}
