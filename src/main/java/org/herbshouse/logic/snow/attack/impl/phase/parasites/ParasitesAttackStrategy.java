package org.herbshouse.logic.snow.attack.impl.phase.parasites;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.impl.AbstractAttackPhaseStrategy;
import org.herbshouse.logic.snow.data.AttackData3;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class ParasitesAttackStrategy extends AbstractAttackPhaseStrategy<AttackData3> {

  private final FlagsConfiguration flagsConfiguration;
  private final Rectangle screenBounds;

  public ParasitesAttackStrategy(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    this.flagsConfiguration = flagsConfiguration;
    this.screenBounds = screenBounds;

    A3Phase1 phase1 = new A3Phase1(this);
    A3Phase2 phase2 = new A3Phase2(this);
    A3Phase3 phase3 = new A3Phase3(this);
    A3Phase4 phase4 = new A3Phase4(this);
    A3Phase5 phase5 = new A3Phase5(this);

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
    }
  }

  @Override
  public int getAttackType() {
    return 3;
  }

  @Override
  public AttackData3 getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData("ATTACK3");
    if (data == null) {
      data = new AttackData3();
      snowflake.setData("ATTACK3", data);
      this.beforeStart(List.of(snowflake));
    }
    return (AttackData3) data;
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
