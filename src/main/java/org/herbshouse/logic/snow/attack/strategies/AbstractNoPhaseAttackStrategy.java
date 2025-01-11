package org.herbshouse.logic.snow.attack.strategies;

import java.util.List;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.data.AbstractAttackData;

public abstract class AbstractNoPhaseAttackStrategy<T extends AbstractAttackData> implements
    AttackStrategy<T> {

  private boolean started = false;

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    started = true;
  }

  @Override
  public abstract T getData(Snowflake snowflake);

  @Override
  public boolean isStarted() {
    return started;
  }

}
