package org.herbshouse.logic;

import java.util.List;

public interface SnowListener {
    void turnOnHappyWind();

    void turnOffHappyWind();

    void turnOnNormalWind();

    void turnOffNormalWind();

    void freezeSnowflakes(List<Snowflake> snowflakes);

    void switchDisplayBigBalls();

    void switchDebug();

    void switchHeavySnowing();

    void switchAttack();

    void mouseMove(Point2D mouseLocation);

}
