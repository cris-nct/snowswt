package org.herbshouse.logic.snow;

import java.util.HashMap;
import java.util.Map;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.SnowflakeData;

public class Snowflake extends AbstractMovableObject {

  private final Map<String, SnowflakeData> data = new HashMap<>();

  private final SnowTail snowTail;
  private boolean freezed = false;
  private AttackStrategy<?> individualStrategy;

  public Snowflake() {
    snowTail = new SnowTail(this);
  }

  public void setIndividualStrategy(AttackStrategy<?> individualStrategy) {
    this.individualStrategy = individualStrategy;
  }

  public AttackStrategy<?> getIndividualStrategy() {
    return individualStrategy;
  }

  public SnowTail getSnowTail() {
    return snowTail;
  }

  public void freeze() {
    if (!freezed) {
      freezed = true;
      setColor(GuiUtils.FREEZE_COLOR);
    }
  }

  public boolean isFreezed() {
    return freezed;
  }

  public SnowflakeData getData(String key) {
    return data.get(key);
  }

  public void setData(String key, SnowflakeData data) {
    this.data.put(key, data);
  }

  public void cleanup() {
    data.clear();
  }

}