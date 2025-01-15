package org.herbshouse.logic.snow;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.nonphase.FireworksStrategy;

class InitialAnimation {

  private final SnowGenerator snowGenerator;

  private int countdown = -1;

  InitialAnimation(SnowGenerator snowGenerator) {
    this.snowGenerator = snowGenerator;
  }

  void run() {
    //noinspection IntegerDivisionInFloatingPointContext
    Point2D location = new Point2D(snowGenerator.getScreenBounds().width / 2,
        snowGenerator.getScreenBounds().height / 2);
    int numberOfFlakes = 50;
    for (int k = 0; k < numberOfFlakes; k++) {
      Snowflake snowflake = new Snowflake();
      snowflake.setLocation(location);
      snowflake.setSize(15);
      snowflake.setColor(new RGB(240, 0, 0));
      snowflake.setAlpha((int) Utils.linearInterpolation(k, 0, 50, numberOfFlakes - 1, 255));
      snowGenerator.addSnowflake(snowflake);
    }
    for (countdown = 10; countdown > 0; countdown--) {
      for (double angle = 0; angle < 360; angle++) {
        for (int k = 0; k < numberOfFlakes; k++) {
          snowGenerator.getSnowflakes().get(k)
              .setLocation(Utils.moveToDirection(location, 200, Math.toRadians(angle + k * 2)));
        }
        Utils.sleep(3);
      }
    }
    countdown = -1;
    snowGenerator.removeSnowflakes(snowGenerator.getSnowflakes());

    //Fireworks
    for (int i = 0; i < 20; i++) {
      Snowflake snowflake = snowGenerator.generateNewSnowflake();
      snowflake.setLocation(new Point2D(snowGenerator.getScreenBounds().width / 2.0, 1));
      AttackStrategy<?> strategy = new FireworksStrategy(snowGenerator.getLogicController()
          .getFlagsConfiguration(), snowGenerator.getScreenBounds());
      strategy.beforeStart(List.of(snowflake));
      snowflake.setIndividualStrategy(strategy);
    }
    snowGenerator.getLogicController().switchObjectsTail();
    while (!snowGenerator.getSnowflakes().isEmpty()) {
      snowGenerator.update();
      Utils.sleep(10);
    }
    snowGenerator.getLogicController().switchObjectsTail();
  }

  int getCountdown() {
    return countdown;
  }

}
