package org.herbshouse.logic.snow;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.herbshouse.audio.AudioPlayOrder;
import org.herbshouse.audio.AudioPlayer;
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
      generateFireworks(1);
    }
    AudioPlayer audioPlayer = snowGenerator.getLogicController().getAudioPlayer();
    audioPlayer.play(new AudioPlayOrder("fireworks.wav", 5000));
    int counter = 0;
    while (!snowGenerator.getSnowflakes().isEmpty()) {
      snowGenerator.update();
      Utils.sleep(10);
      counter++;
      if (counter % 180 == 0 && counter >= 300 && counter < 2000) {
        generateFireworks(2);
        generateFireworks(2);
        generateFireworks(2);
        audioPlayer.play(new AudioPlayOrder("fireworks-2.wav", 4000));
      }
    }
    audioPlayer.stop("fireworks.wav");
    audioPlayer.stop("fireworks-2.wav");
  }

  private void generateFireworks(int phase) {
    Snowflake snowflake = snowGenerator.generateNewSnowflake();
    snowflake.setSize(7);
    snowflake.setLocation(new Point2D(snowGenerator.getScreenBounds().width / 2.0, 1));
    AttackStrategy<?> strategy = new FireworksStrategy(snowGenerator.getLogicController()
        .getFlagsConfiguration(), snowGenerator.getScreenBounds());
    strategy.beforeStart(List.of(snowflake));
    snowflake.setIndividualStrategy(strategy);
    if (phase == 1) {
      if (Math.random() < 0.5) {
        snowflake.setColor(new RGB(255, 255, 0));
      } else {
        snowflake.setColor(new RGB(255, 0, 0));
      }
    } else if (phase == 2) {
      snowflake.setColor(new RGB(0, 255, 0));
    }
  }

  int getCountdown() {
    return countdown;
  }

}
