package org.herbshouse.logic.snow.attack.attack3;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.strategies.AbstractAttackPhaseStrategy;

public class ParasitesAttackStrategy extends AbstractAttackPhaseStrategy<AttackData3> {

  public ParasitesAttackStrategy(FlagsConfiguration flagsConfiguration, Rectangle screenBounds) {
    A3Phase1 phase1 = new A3Phase1(flagsConfiguration, screenBounds);
    A3Phase2 phase2 = new A3Phase2(flagsConfiguration, screenBounds);
    A3Phase3 phase3 = new A3Phase3(flagsConfiguration, screenBounds);
    A3Phase4 phase4 = new A3Phase4(flagsConfiguration, screenBounds);
    A3Phase5 phase5 = new A3Phase5(flagsConfiguration, screenBounds);

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
    return snowflake.getAttackData3();
  }

}
