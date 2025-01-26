package org.herbshouse.logic.snow.attack.impl.phase.parasites;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.audio.AudioPlayer;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractAttackPhaseStrategy;
import org.herbshouse.logic.snow.data.AttackDataParasites;

public class ParasitesAttackStrategy extends AbstractAttackPhaseStrategy<AttackDataParasites> {

  private final FlagsConfiguration flagsConfiguration;
  private final Rectangle screenBounds;

  public ParasitesAttackStrategy(FlagsConfiguration flagsConfiguration, Rectangle screenBounds, AudioPlayer audioPlayer) {
    super(audioPlayer);
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;

    ParasitesPhase1 phase1 = new ParasitesPhase1(this);
    ParasitesPhase2 phase2 = new ParasitesPhase2(this);
    ParasitesPhase3 phase3 = new ParasitesPhase3(this);
    ParasitesPhase4 phase4 = new ParasitesPhase4(this);
    ParasitesPhase5 phase5 = new ParasitesPhase5(this);

    phase1.setNextPhase(phase2);
    phase2.setNextPhase(phase3);
    phase3.setNextPhase(phase4);
    phase4.setNextPhase(phase5);
    phase5.setNextPhase(phase1);

    this.addPhases(phase1, phase2, phase3, phase4, phase5);
  }

  @Override
  public void shutdown() {

  }

  @Override
  public int getMaxSnowflakesInvolved() {
    return 20;
  }

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    super.beforeStart(snowflakeList);
    for (Snowflake snowflake : snowflakeList) {
      snowflake.getSnowTail().setTailLength(150);
      snowflake.setShowTrail(true);
    }
  }

  @Override
  public Class<AttackDataParasites> getDataClass() {
    return AttackDataParasites.class;
  }

  @Override
  public int getAttackType() {
    return 3;
  }

  @Override
  public FlagsConfiguration getFlagsConfiguration() {
    return flagsConfiguration;
  }

  @Override
  public Rectangle getScreenBounds() {
    return screenBounds;
  }

}
