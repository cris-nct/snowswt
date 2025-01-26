package org.herbshouse.logic.snow;

import java.util.List;
import org.herbshouse.audio.AudioPlayOrder;
import org.herbshouse.audio.AudioPlayType;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.impl.phase.blackhole.BlackHoleStrategy;

public class BlackHoleModeController {

  private final SnowGenerator snowGenerator;

  public BlackHoleModeController(SnowGenerator snowGenerator) {
    this.snowGenerator = snowGenerator;
  }

  public void start() {
    AudioPlayOrder order = new AudioPlayOrder("sounds/blackhole.wav");
    order.setType(AudioPlayType.BACKGROUND);
    order.setVolume(0.9f);
    snowGenerator.getLogicController().getAudioPlayer().play(order);
  }

  public void playAudio2Background() {
    AudioPlayOrder order = new AudioPlayOrder("sounds/blackhole-2.wav");
    order.setType(AudioPlayType.BACKGROUND);
    order.setVolume(0.9f);
    snowGenerator.getLogicController().getAudioPlayer().play(order);
  }

  public void stop() {
    stopBackgroundSounds();
    for (Snowflake snowflake : snowGenerator.getSnowflakes()) {
      if (snowflake.getIndividualStrategy() != null) {
        snowflake.getIndividualStrategy().shutdown();
        snowflake.setIndividualStrategy(null);
        snowflake.setShowTrail(false);
        snowflake.setAlpha(255);
        snowflake.setSize(5);
      }
    }
  }

  private void stopBackgroundSounds() {
    snowGenerator.getLogicController().getAudioPlayer().stop("sounds/blackhole.wav");
    snowGenerator.getLogicController().getAudioPlayer().stop("sounds/blackhole-2.wav");
  }

  public void playExplosion() {
    stopBackgroundSounds();
    if (!snowGenerator.getLogicController().getAudioPlayer().isPlaying("sounds/explosion.wav")) {
      AudioPlayOrder order = new AudioPlayOrder("sounds/explosion.wav");
      order.setType(AudioPlayType.EFFECT);
      order.setVolume(1f);
      snowGenerator.getLogicController().getAudioPlayer().play(order);
    }
  }

  public boolean shouldSwallowPoint(Point2D location) {
    return Utils.distance(snowGenerator.getFlagsConfiguration().getMouseLoc(), location) < BlackHoleStrategy.BLACKHOLE_RADIUS;
  }

  public void afterGenerateSnowflake(Snowflake snowflake) {
    long snowflakesBlackHole = snowGenerator.getSnowflakes().stream().filter(s -> s.getIndividualStrategy() instanceof BlackHoleStrategy).count();
    if (snowflakesBlackHole < BlackHoleStrategy.BLACKHOLES_MAX_SNOWFLAKES) {
      boolean stillOneSnowflakeToFinish = snowGenerator.getSnowflakes().stream()
          .anyMatch(s -> s.getIndividualStrategy() instanceof BlackHoleStrategy
              && ((BlackHoleStrategy) s.getIndividualStrategy()).getCurrentPhaseProcessor().getCurrentPhaseIndex() == 6
              && !((BlackHoleStrategy) s.getIndividualStrategy()).getCurrentPhaseProcessor().isFinished(s)
          );
      if (!stillOneSnowflakeToFinish) {
        AttackStrategy<?> strategy = new BlackHoleStrategy(
            snowGenerator.getFlagsConfiguration(),
            snowGenerator.getScreenBounds(),
            this
        );
        strategy.beforeStart(List.of(snowflake));
        snowflake.setIndividualStrategy(strategy);
      }
    }
  }


}
