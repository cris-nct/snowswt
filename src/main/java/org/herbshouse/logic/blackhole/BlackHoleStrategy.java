package org.herbshouse.logic.blackhole;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.audio.AudioPlayer;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractAttackPhaseStrategy;
import org.herbshouse.logic.snow.data.AttackDataBlackHole;

public class BlackHoleStrategy extends AbstractAttackPhaseStrategy<AttackDataBlackHole> {

  public static final int BLACKHOLES_MAX_SNOWFLAKES = 500;
  public static final double BLACKHOLE_RADIUS = 150;
  public static final double BLACKHOLE_RING_WIDTH = 30;

  private final FlagsConfiguration flagsConfiguration;
  private final Rectangle screenBounds;
  private double alpha = 255;
  private int alphaDirection = -1;

  public BlackHoleStrategy(
      FlagsConfiguration flagsConfiguration,
      Rectangle screenBounds,
      AudioPlayer audioPlayer
  ) {
    super(audioPlayer);
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;

    BlackHolePhase1BornToWhite phase1 = new BlackHolePhase1BornToWhite(this);
    BlackHolePhase2WhiteRing phase2 = new BlackHolePhase2WhiteRing(this);
    BlackHolePhase3WhiteToYellow phase3 = new BlackHolePhase3WhiteToYellow(this);
    BlackHolePhase4YellowRing phase4 = new BlackHolePhase4YellowRing(this);
    BlackHolePhase5RedRing phase5 = new BlackHolePhase5RedRing(this);
    BlackHolePhase6Explosion phase6 = new BlackHolePhase6Explosion(this);

    phase1.setNextPhase(phase2);
    phase2.setNextPhase(phase3);
    phase3.setNextPhase(phase4);
    phase4.setNextPhase(phase5);
    phase5.setNextPhase(phase6);

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
  public void beforeStart(List<Snowflake> snowflakeList) {
    super.beforeStart(snowflakeList);
    alpha = 255;
    alphaDirection = -1;
  }

  @Override
  public Class<AttackDataBlackHole> getDataClass() {
    return AttackDataBlackHole.class;
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
  public void afterUpdate(List<Snowflake> snowflakeList) {
    super.afterUpdate(snowflakeList);
    alpha += 10 * alphaDirection;
    if (alpha < 50 || alpha >= 255) {
      alphaDirection = -alphaDirection;
    }
  }

  public Point2D generateRandomPointOutside() {
    Point2D loc = new Point2D(Math.random() * screenBounds.width, Math.random() * screenBounds.height);
    double dist = Utils.distance(0, 0, screenBounds.width, screenBounds.height);
    loc = Utils.moveToDirection(loc, dist, Math.toRadians(Math.random() * 360));
    return loc;
  }

  public int getAlpha() {
    return (int) alpha;
  }

}
