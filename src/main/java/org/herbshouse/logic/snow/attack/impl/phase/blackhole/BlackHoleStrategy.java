package org.herbshouse.logic.snow.attack.impl.phase.blackhole;

import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.audio.AudioPlayOrder;
import org.herbshouse.audio.AudioPlayType;
import org.herbshouse.audio.AudioPlayer;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractAttackPhaseStrategy;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class BlackHoleStrategy extends AbstractAttackPhaseStrategy<AttackDataBlackHole> {

  public static final int BLACKHOLES_MAX_SNOWFLAKES = 100;
  public static final double BLACKHOLE_RADIUS = 150;
  public static final double BLACKHOLE_RING_WIDTH = 30;

  private final FlagsConfiguration flagsConfiguration;
  private final Rectangle screenBounds;
  private final AudioPlayer audioPlayer;
  private double alpha = 255;
  private int alphaDirection = -1;
  private boolean playedAudioExplosion;

  public BlackHoleStrategy(FlagsConfiguration flagsConfiguration, Rectangle screenBounds, AudioPlayer audioPlayer) {
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;
    this.audioPlayer = audioPlayer;

    BlackHolePhase1 phase1 = new BlackHolePhase1(this);
    BlackHolePhase2 phase2 = new BlackHolePhase2(this);
    BlackHolePhase3 phase3 = new BlackHolePhase3(this);
    BlackHolePhase4 phase4 = new BlackHolePhase4(this);
    BlackHolePhase5 phase5 = new BlackHolePhase5(this);
    BlackHolePhase6 phase6 = new BlackHolePhase6(this);

    phase1.setNextPhase(phase2);
    phase2.setNextPhase(phase3);
    phase3.setNextPhase(phase4);
    phase4.setNextPhase(phase5);
    phase5.setNextPhase(phase6);
    phase6.setNextPhase(phase6);

    this.addPhases(phase1, phase2, phase3, phase4, phase5, phase6);
  }

  @Override
  public int getMaxSnowflakesInvolved() {
    return BLACKHOLES_MAX_SNOWFLAKES;
  }

  @Override
  public int getAttackType() {
    return 5;
  }

  @Override
  public AttackDataBlackHole getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData("ATTACKDATABLACKHOLE");
    if (data == null) {
      data = new AttackDataBlackHole();
      snowflake.setData("ATTACKDATABLACKHOLE", data);
      this.beforeStart(List.of(snowflake));
    }
    return (AttackDataBlackHole) data;
  }

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    super.beforeStart(snowflakeList);
    alpha = 255;
    alphaDirection = -1;
    playedAudioExplosion = false;
  }

  @Override
  public FlagsConfiguration getFlagsConfiguration() {
    return flagsConfiguration;
  }

  @Override
  public Rectangle getScreenBounds() {
    return screenBounds;
  }

  @Override
  public void shutdown() {

  }

  @Override
  public void afterUpdate(List<Snowflake> snowflakeList) {
    super.afterUpdate(snowflakeList);
    alpha += 10 * alphaDirection;
    if (alpha < 50 || alpha >= 255) {
      alphaDirection = -alphaDirection;
    }
    boolean isIndividualStrategy = false;
    for (Snowflake snowflake : snowflakeList) {
      isIndividualStrategy = isIndividualStrategy || snowflake.getIndividualStrategy() instanceof BlackHoleStrategy;
      if (getCurrentPhaseProcessor().getCurrentPhaseIndex() == 6
          && getCurrentPhaseProcessor().isFinished(snowflake)) {
        snowflake.setIndividualStrategy(null);
        snowflake.setColor(new RGB(255, 255, 255));
        snowflake.setSize(5);
        snowflake.setShowTrail(false);
        snowflake.cleanup();
      }
    }

    if (getCurrentPhaseProcessor().getCurrentPhaseIndex() == 6 && !playedAudioExplosion) {
      if (isIndividualStrategy) {
        AudioPlayOrder order = new AudioPlayOrder("bigexplosion.wav");
        order.setType(AudioPlayType.EFFECT);
        order.setVolume(1f);
        audioPlayer.play(order);
      } else {
        AudioPlayOrder order = new AudioPlayOrder("extendedexplosion.wav");
        order.setType(AudioPlayType.EFFECT);
        order.setVolume(1f);
        audioPlayer.play(order);
      }
      playedAudioExplosion = true;
    }
  }

  public int getAlpha() {
    return (int) alpha;
  }
}
